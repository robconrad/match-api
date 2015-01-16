/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 5:43 PM
 */

package base.entity.user.impl

import base.common.lib.Dispatchable
import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.error.ApiError
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.CrudErrorImplicits
import base.entity.user._
import base.entity.user.impl.RegisterCommandServiceImpl.Errors
import base.entity.user.kv.{ UserKeyService, PhoneKeyService, PhoneKey, PhoneCooldownKeyService }
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }
import spray.http.StatusCodes

import scala.concurrent.duration.FiniteDuration

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class RegisterCommandServiceImpl(phoneCooldown: FiniteDuration)
    extends CommandServiceImpl[RegisterModel, RegisterResponseModel]
    with RegisterCommandService {

  def innerExecute(input: RegisterModel)(implicit authCtx: AuthContext) = {
    new RegisterCommand(input).execute()
  }

  /**
   * - reject if phone in cooldown
   * - set phone cooldown
   * - expire phone cooldown
   * - createNX phone key attributes, createNX user key attributes
   * - update phone key attributes
   * - send SMS verification
   */
  private[impl] class RegisterCommand(val input: RegisterModel)(implicit val authCtx: AuthContext)
      extends Command[RegisterModel, RegisterResponseModel] {

    def execute() = {
      phoneCooldownExists(PhoneCooldownKeyService().make(KeyId(input.phone)))
    }

    def phoneCooldownExists(key: IntKey): Response =
      key.exists().flatMap {
        case false => phoneCooldownSet(key)
        case true  => Errors.phoneCooldown
      }

    def phoneCooldownSet(key: IntKey): Response =
      key.set(RegisterCommandServiceImpl.phoneCooldownValue).flatMap {
        case true  => phoneCooldownExpire(key)
        case false => Errors.phoneCooldownSetFailed
      }

    def phoneCooldownExpire(key: IntKey): Response =
      key.expire(phoneCooldown.toSeconds).flatMap {
        case true  => phoneCreate()
        case false => Errors.phoneCooldownExpireFailed
      }

    def phoneCreate(): Response = {
      val phoneKey = PhoneKeyService().make(KeyId(input.phone))
      phoneKey.create().flatMap { exists =>
        phoneKey.getUserId.flatMap {
          case Some(userId) => phoneSetCode(phoneKey)
          case None         => userCreate(phoneKey)
        }
      }
    }

    def userCreate(key: PhoneKey): Response = {
      val userId = RandomService().uuid
      UserKeyService().make(KeyId(userId)).create().flatMap {
        case false => Errors.userSetFailed
        case true =>
          key.setUserId(userId).flatMap {
            case true  => phoneSetCode(key)
            case false => Errors.userSetFailed
          }
      }
    }

    def phoneSetCode(key: PhoneKey): Response = {
      val code = VerifyCommandService().makeVerifyCode()
      key.setCode(code).flatMap {
        case true  => smsSend(code: String)
        case false => Errors.phoneSetFailed
      }
    }

    def smsSend(code: String): Response =
      VerifyCommandService().sendVerifySms(input.phone, code).map {
        case true  => RegisterResponseModel()
        case false => Errors.smsSendFailed
      }

  }

}

object RegisterCommandServiceImpl {

  val phoneCooldownValue = 1

  object Errors extends CrudErrorImplicits[RegisterResponseModel] {

    protected val externalErrorText = "There was a problem with registration."

    private val phoneCooldownText = "This phone number has been registered too recently."

    lazy val phoneCooldown: Response = (phoneCooldownText, StatusCodes.EnhanceYourCalm, PHONE_RATE_LIMIT)
    lazy val phoneCooldownSetFailed: Response = "failed to set phoneCooldown"
    lazy val phoneCooldownExpireFailed: Response = "failed to expire phoneCooldown"
    lazy val userSetFailed: Response = "failed to create user"
    lazy val phoneSetFailed: Response = "failed to set phone updates"
    lazy val smsSendFailed: ApiError = "failed to send sms"

  }

}

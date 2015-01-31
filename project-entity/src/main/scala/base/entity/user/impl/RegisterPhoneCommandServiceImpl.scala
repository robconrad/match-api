/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:40 AM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.error.ApiError
import base.entity.service.CrudErrorImplicits
import base.entity.user._
import base.entity.user.impl.RegisterPhoneCommandServiceImpl.Errors
import base.entity.user.kv._
import base.entity.user.model.{ RegisterPhoneModel, RegisterPhoneResponseModel }
import spray.http.StatusCodes

import scala.concurrent.duration.FiniteDuration

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class RegisterPhoneCommandServiceImpl(phoneCooldown: FiniteDuration)
    extends CommandServiceImpl[RegisterPhoneModel, RegisterPhoneResponseModel]
    with RegisterPhoneCommandService {

  override protected val responseManifest = Option(manifest[RegisterPhoneResponseModel])

  def innerExecute(input: RegisterPhoneModel)(implicit channelCtx: ChannelContext) = {
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
  private[impl] class RegisterCommand(val input: RegisterPhoneModel)(implicit val channelCtx: ChannelContext)
      extends Command[RegisterPhoneModel, RegisterPhoneResponseModel] {

    def execute() = {
      phoneCooldownExists(PhoneCooldownKeyService().make(input.phone))
    }

    def phoneCooldownExists(key: PhoneCooldownKey): Response =
      key.exists().flatMap {
        case false => phoneCooldownSet(key)
        case true  => Errors.phoneCooldown
      }

    def phoneCooldownSet(key: PhoneCooldownKey): Response =
      key.set(RegisterPhoneCommandServiceImpl.phoneCooldownValue).flatMap {
        case true  => phoneCooldownExpire(key)
        case false => Errors.phoneCooldownSetFailed
      }

    def phoneCooldownExpire(key: PhoneCooldownKey): Response =
      key.expire(phoneCooldown.toSeconds).flatMap {
        case true  => phoneCreate()
        case false => Errors.phoneCooldownExpireFailed
      }

    def phoneCreate(): Response = {
      val phoneKey = PhoneKeyService().make(input.phone)
      phoneKey.create().flatMap { exists =>
        phoneKey.getUserId.flatMap {
          case Some(userId) => phoneSetCode(phoneKey)
          case None =>
            val userId = RandomService().uuid
            userCreate(userId, UserKeyService().make(userId), phoneKey)
        }
      }
    }

    def userCreate(userId: UUID, userKey: UserKey, phoneKey: PhoneKey): Response = {
      userKey.create().flatMap {
        case false => Errors.userSetFailed
        case true =>
          phoneKey.setUserId(userId).flatMap {
            case true  => phoneSetCode(phoneKey)
            case false => Errors.userSetFailed
          }
      }
    }

    def phoneSetCode(key: PhoneKey): Response = {
      val code = VerifyPhoneCommandService().makeVerifyCode()
      key.setCode(code).flatMap {
        case true  => smsSend(code: String)
        case false => Errors.phoneSetFailed
      }
    }

    def smsSend(code: String): Response =
      VerifyPhoneCommandService().sendVerifySms(input.phone, code).map {
        case true  => RegisterPhoneResponseModel(input.phone)
        case false => Errors.smsSendFailed
      }

  }

}

object RegisterPhoneCommandServiceImpl {

  val phoneCooldownValue = 1

  object Errors extends CrudErrorImplicits[RegisterPhoneResponseModel] {

    override protected val externalErrorText = "There was a problem with registration."

    private val phoneCooldownText = "This phone number has been registered too recently."

    lazy val phoneCooldown: Response = (phoneCooldownText, StatusCodes.EnhanceYourCalm, PHONE_RATE_LIMIT)
    lazy val phoneCooldownSetFailed: Response = "failed to set phoneCooldown"
    lazy val phoneCooldownExpireFailed: Response = "failed to expire phoneCooldown"
    lazy val userSetFailed: Response = "failed to create user"
    lazy val phoneSetFailed: Response = "failed to set phone updates"
    lazy val smsSendFailed: ApiError = "failed to send sms"

  }

}

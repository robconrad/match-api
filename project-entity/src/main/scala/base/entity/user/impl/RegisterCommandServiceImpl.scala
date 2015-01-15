/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:01 AM
 */

package base.entity.user.impl

import base.common.lib.Dispatchable
import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.{ CrudErrorImplicits, CrudImplicits }
import base.entity.user.RegisterCommandService.RegisterResponse
import base.entity.user._
import base.entity.user.impl.RegisterCommandServiceImpl.Errors
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }
import spray.http.StatusCodes

import scala.concurrent.duration.FiniteDuration

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class RegisterCommandServiceImpl(phoneCooldown: FiniteDuration)
    extends ServiceImpl
    with RegisterCommandService
    with CrudImplicits[RegisterResponseModel]
    with Dispatchable
    with AuthLoggable {

  private implicit val ec = dispatcher

  /**
   * - reject if phone in cooldown
   * - set phone cooldown
   * - expire phone cooldown
   * - createNX phone key attributes, createNX user key attributes
   * - update phone key attributes
   * - send SMS verification
   */
  def register(input: RegisterModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.REGISTER)
    phoneCooldownExists(PhoneCooldownKeyService().make(KeyId(input.phone)))(input, KvFactoryService().pipeline)
  }

  private[impl] def phoneCooldownExists(phoneCooldownKey: IntKey)(implicit input: RegisterModel,
                                                                  p: Pipeline): RegisterResponse = {
    phoneCooldownKey.exists().flatMap {
      case false => phoneCooldownSet(phoneCooldownKey)
      case true  => Errors.phoneCooldown
    }
  }

  private[impl] def phoneCooldownSet(phoneCooldownKey: IntKey)(implicit input: RegisterModel,
                                                               p: Pipeline): RegisterResponse = {
    phoneCooldownKey.set(RegisterCommandServiceImpl.phoneCooldownValue).flatMap {
      case true  => phoneCooldownExpire(phoneCooldownKey)
      case false => Errors.phoneCooldownSetFailed
    }
  }

  private[impl] def phoneCooldownExpire(phoneCooldownKey: IntKey)(implicit input: RegisterModel,
                                                                  p: Pipeline): RegisterResponse = {
    phoneCooldownKey.expire(phoneCooldown.toSeconds).flatMap {
      case true  => phoneKeyCreate()
      case false => Errors.phoneCooldownExpireFailed
    }
  }

  private[impl] def phoneKeyCreate()(implicit input: RegisterModel,
                                     p: Pipeline): RegisterResponse = {
    val phoneKey = PhoneKeyService().make(KeyId(input.phone))
    phoneKey.create().flatMap { exists =>
      phoneKey.getUserId.flatMap {
        case Some(userId) => phoneKeyUpdates(phoneKey)
        case None         => userKeyCreate(phoneKey)
      }
    }
  }

  private[impl] def userKeyCreate(phoneKey: PhoneKey)(implicit input: RegisterModel,
                                                      p: Pipeline): RegisterResponse = {
    val userId = RandomService().uuid
    UserKeyService().make(KeyId(userId)).create().flatMap {
      case false => Errors.userSetFailed
      case true =>
        phoneKey.setUserId(userId).flatMap {
          case true  => phoneKeyUpdates(phoneKey)
          case false => Errors.userSetFailed
        }
    }
  }

  private[impl] def phoneKeyUpdates(phoneKey: PhoneKey)(implicit input: RegisterModel,
                                                        p: Pipeline): RegisterResponse = {
    val code = VerifyCommandService().makeVerifyCode()
    phoneKey.setCode(code).flatMap {
      case true  => smsSend(code: String)
      case false => Errors.phoneSetFailed
    }
  }

  private[impl] def smsSend(code: String)(implicit input: RegisterModel,
                                          p: Pipeline): RegisterResponse = {
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

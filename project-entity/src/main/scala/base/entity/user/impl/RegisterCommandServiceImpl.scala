/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 9:09 PM
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
import base.entity.service.CrudImplicits
import base.entity.user.RegisterCommandService.RegisterResponse
import base.entity.user._
import base.entity.user.impl.RegisterCommandServiceImpl._
import base.entity.user.model._
import spray.http.StatusCodes._

import scala.concurrent.duration.FiniteDuration

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class RegisterCommandServiceImpl(phoneCooldown: FiniteDuration)
    extends ServiceImpl with RegisterCommandService with Dispatchable with AuthLoggable {

  private implicit val ec = dispatcher

  private val crudImplicits = CrudImplicits[RegisterResponseModel]
  import crudImplicits._

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
      case true  => externalErrorPhoneResponse
      case false => phoneCooldownSet(phoneCooldownKey)
    }
  }

  private[impl] def phoneCooldownSet(phoneCooldownKey: IntKey)(implicit input: RegisterModel,
                                                               p: Pipeline): RegisterResponse = {
    phoneCooldownKey.set(phoneCooldownValue).flatMap {
      case true  => phoneCooldownExpire(phoneCooldownKey)
      case false => internalErrorSetPhoneCooldownResponse
    }
  }

  private[impl] def phoneCooldownExpire(phoneCooldownKey: IntKey)(implicit input: RegisterModel,
                                                                  p: Pipeline): RegisterResponse = {
    phoneCooldownKey.expire(phoneCooldown.toSeconds).flatMap {
      case true  => phoneKeyCreate()
      case false => internalErrorExpirePhoneCooldownResponse
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
      case false => internalErrorUserCreateResponse
      case true =>
        phoneKey.setUserId(userId).flatMap {
          case true  => phoneKeyUpdates(phoneKey)
          case false => internalErrorUserCreateResponse
        }
    }
  }

  private[impl] def phoneKeyUpdates(phoneKey: PhoneKey)(implicit input: RegisterModel,
                                                        p: Pipeline): RegisterResponse = {
    val code = VerifyCommandService().makeVerifyCode()
    phoneKey.setCode(code).flatMap {
      case true  => smsSend(code: String)
      case false => internalErrorPhoneUpdatesResponse
    }
  }

  private[impl] def smsSend(code: String)(implicit input: RegisterModel,
                                          p: Pipeline): RegisterResponse = {
    VerifyCommandService().sendVerifySms(input.phone, code).map {
      case true  => RegisterResponseModel()
      case false => internalErrorSmsFailedResponse
    }
  }

}

object RegisterCommandServiceImpl {

  private val crudImplicits = CrudImplicits[RegisterResponseModel]
  import base.entity.user.impl.RegisterCommandServiceImpl.crudImplicits._

  val phoneCooldownValue = 1

  lazy val externalErrorPhone = "This phone number has been registered too recently."
  lazy val externalErrorRegister = "There was a problem with registration."

  lazy val internalErrorSetPhoneCooldown = "failed to set phoneCooldown"
  lazy val internalErrorExpirePhoneCooldown = "failed to expire phoneCooldown"
  lazy val internalErrorUserCreate = "failed to create user"
  lazy val internalErrorPhoneUpdates = "failed to set phone updates"
  lazy val internalErrorSmsFailed = "failed to send sms"

  lazy val externalErrorPhoneResponse: RegisterResponse =
    ApiError(externalErrorPhone, EnhanceYourCalm, PHONE_RATE_LIMIT)
  lazy val internalErrorSetPhoneCooldownResponse: RegisterResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorSetPhoneCooldown)
  lazy val internalErrorExpirePhoneCooldownResponse: RegisterResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorExpirePhoneCooldown)
  lazy val internalErrorUserCreateResponse: RegisterResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorUserCreate)
  lazy val internalErrorPhoneUpdatesResponse: RegisterResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorPhoneUpdates)
  lazy val internalErrorSmsFailedResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorSmsFailed)

}

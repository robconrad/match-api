/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 7:06 PM
 */

package base.entity.user.impl

import base.common.lib.Dispatchable
import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.common.time.TimeService
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.kv.Key.{ Pipeline, Prop }
import base.entity.kv.KeyProps._
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.CrudImplicits
import base.entity.user.RegisterService.RegisterResponse
import base.entity.user.UserKeyFactories.{ PhoneCooldownKeyFactory, PhoneKeyFactory, UserKeyFactory }
import base.entity.user.UserKeyProps.{ CodeProp, UserIdProp }
import base.entity.user.impl.RegisterServiceImpl._
import base.entity.user.model._
import base.entity.user.{ RegisterService, VerifyService }
import spray.http.StatusCodes._

import scala.concurrent.duration.FiniteDuration

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class RegisterServiceImpl(phoneCooldown: FiniteDuration)
    extends ServiceImpl with RegisterService with Dispatchable with AuthLoggable {

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
    phoneCooldownExists(PhoneCooldownKeyFactory().make(KeyId(input.phone)))(input, KvService().pipeline)
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
    val phoneKey = PhoneKeyFactory().make(KeyId(input.phone))
    phoneKey.setNx(CreatedProp, TimeService().asString()).flatMap { exists =>
      phoneKey.getId(UserIdProp).flatMap {
        case Some(userId) => phoneKeyUpdates(phoneKey)
        case None         => userKeyCreate(phoneKey)
      }
    }
  }

  private[impl] def userKeyCreate(phoneKey: HashKey)(implicit input: RegisterModel,
                                                     p: Pipeline): RegisterResponse = {
    val userId = RandomService().uuid
    phoneKey.set(UserIdProp, userId).flatMap {
      case true =>
        val userKey = UserKeyFactory().make(KeyId(userId))
        userKey.setNx(CreatedProp, TimeService().now).flatMap { exists =>
          phoneKeyUpdates(phoneKey)
        }
      case false => internalErrorUserCreateResponse
    }
  }

  private[impl] def phoneKeyUpdates(phoneKey: HashKey)(implicit input: RegisterModel,
                                                       p: Pipeline): RegisterResponse = {
    val code = VerifyService().makeVerifyCode()
    val props = Map[Prop, Any](
      UpdatedProp -> TimeService().asString(),
      CodeProp -> code)
    phoneKey.set(props).flatMap {
      case true  => smsSend(code: String)
      case false => internalErrorPhoneUpdatesResponse
    }
  }

  private[impl] def smsSend(code: String)(implicit input: RegisterModel,
                                          p: Pipeline): RegisterResponse = {
    VerifyService().sendVerifySms(input.phone, code).map {
      case true  => RegisterResponseModel()
      case false => internalErrorSmsFailedResponse
    }
  }

}

object RegisterServiceImpl {

  private val crudImplicits = CrudImplicits[RegisterResponseModel]
  import base.entity.user.impl.RegisterServiceImpl.crudImplicits._

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

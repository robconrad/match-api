/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 12:23 PM
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
import base.entity.sms.SmsService
import base.entity.user.RegisterService
import base.entity.user.RegisterService.RegisterResponse
import base.entity.user.impl.RegisterServiceImpl._
import base.entity.user.model._
import spray.http.StatusCodes._

import scala.concurrent.duration.FiniteDuration

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class RegisterServiceImpl(phoneCooldown: FiniteDuration,
                                          verifyCodeLength: Int,
                                          verifySmsBody: String)
    extends ServiceImpl with RegisterService with Dispatchable with AuthLoggable {

  private implicit val ec = dispatcher

  private val crudImplicits = CrudImplicits[RegisterResponseModel]
  import crudImplicits._

  private[impl] val phoneCooldownFactory = KvService().makeIntKeyFactory("phoneCooldown")
  private[impl] val phoneFactory = KvService().makeHashKeyFactory("phone")

  private[impl] val CODE = KeyProp("code")

  def register(input: RegisterModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.REGISTER)
    phoneCooldownExists()(input, KvService().pipeline)
  }

  private[impl] def phoneCooldownExists()(implicit input: RegisterModel,
                                          p: Pipeline): RegisterResponse = {
    val phoneCooldownKey = phoneCooldownFactory.make(KeyId(input.phone))
    phoneCooldownKey.exists().flatMap {
      case true  => externalErrorPhoneResponse
      case false => phoneCooldownSet(phoneCooldownKey)
    }
  }

  private[impl] def phoneCooldownSet(phoneCooldownKey: IntKey)(implicit input: RegisterModel,
                                                               p: Pipeline): RegisterResponse = {
    phoneCooldownKey.set(1).flatMap {
      case false => internalErrorSetPhoneCooldownResponse
      case true  => phoneCooldownExpire(phoneCooldownKey)
    }
  }

  private[impl] def phoneCooldownExpire(phoneCooldownKey: IntKey)(implicit input: RegisterModel,
                                                                  p: Pipeline): RegisterResponse = {
    phoneCooldownKey.expire(phoneCooldown.toSeconds).flatMap {
      case false => internalErrorExpirePhoneCooldownResponse
      case true  => phoneKeySetNx()
    }
  }

  private[impl] def phoneKeySetNx()(implicit input: RegisterModel,
                                    p: Pipeline): RegisterResponse = {
    val phoneKey = phoneFactory.make(KeyId(input.phone))
    phoneKey.setNx(CREATED, TimeService().asString()).flatMap { exists =>
      phoneKeySet(phoneKey: HashKey)
    }
  }

  private[impl] def phoneKeySet(phoneKey: HashKey)(implicit input: RegisterModel,
                                                   p: Pipeline): RegisterResponse = {
    val code = makeVerifyCode()
    val props = Map[Prop, Any](
      UPDATED -> TimeService().asString(),
      CODE -> code)
    phoneKey.set(props).flatMap {
      case false => internalErrorPhoneUpdatesResponse
      case true  => smsSend(code: String)
    }
  }

  private[impl] def smsSend(code: String)(implicit input: RegisterModel,
                                          p: Pipeline): RegisterResponse = {
    SmsService().send(input.phone, verifySmsBody.format(code)).map {
      case false => internalErrorSmsFailedResponse
      case true  => RegisterResponseModel()
    }
  }

  private[impl] def makeVerifyCode() = RandomService().md5.toString.substring(0, verifyCodeLength).toUpperCase

}

object RegisterServiceImpl {

  private val crudImplicits = CrudImplicits[RegisterResponseModel]
  import base.entity.user.impl.RegisterServiceImpl.crudImplicits._

  val externalErrorPhone = "This phone number has been registered too recently."
  val externalErrorRegister = "There was a problem with registration."

  val internalErrorSetPhoneCooldown = "failed to set phoneCooldown"
  val internalErrorExpirePhoneCooldown = "failed to expire phoneCooldown"
  val internalErrorPhoneUpdates = "failed to set phone updates"
  val internalErrorSmsFailed = "failed to send sms"

  val externalErrorPhoneResponse: RegisterResponse =
    ApiError(externalErrorPhone, EnhanceYourCalm, PHONE_RATE_LIMIT)
  val internalErrorSetPhoneCooldownResponse: RegisterResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorSetPhoneCooldown)
  val internalErrorExpirePhoneCooldownResponse: RegisterResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorExpirePhoneCooldown)
  val internalErrorPhoneUpdatesResponse: RegisterResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorPhoneUpdates)
  val internalErrorSmsFailedResponse =
    ApiError(externalErrorRegister, InternalServerError, internalErrorSmsFailed)

}

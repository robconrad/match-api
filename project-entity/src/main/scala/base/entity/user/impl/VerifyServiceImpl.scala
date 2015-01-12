/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 6:19 PM
 */

package base.entity.user.impl

import base.common.lib.Dispatchable
import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.common.time.TimeService
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.kv.Key._
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.{ HashKey, KeyId, KvService }
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.CrudImplicits
import base.entity.sms.SmsService
import base.entity.user.UserKeyFactories.{ DeviceKeyFactory, PhoneKeyFactory, UserKeyFactory }
import base.entity.user.UserKeyProps._
import base.entity.user.VerifyService
import base.entity.user.VerifyService.VerifyResponse
import base.entity.user.impl.VerifyServiceImpl._
import base.entity.user.model._
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class VerifyServiceImpl(codeLength: Int,
                                        smsBody: String)
    extends ServiceImpl with VerifyService with Dispatchable with AuthLoggable {

  private implicit val ec = dispatcher

  private val crudImplicits = CrudImplicits[VerifyResponseModel]
  import crudImplicits._

  /**
   * - get code
   * - verify code
   * - get userId
   * - get user attributes, reject if none exist and none supplied
   * - store attributes on user
   * - create device
   * - store attributes on device with token
   */
  def verify(input: VerifyModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.VERIFY)
    phoneKeyGet()(input, KvService().pipeline)
  }

  def sendVerifySms(phone: String, code: String) = {
    SmsService().send(phone, smsBody.format(code))
  }

  def makeVerifyCode() = {
    RandomService().md5.toString.substring(0, codeLength).toUpperCase
  }

  def validateVerifyCodes(code1: String, code2: String) = {
    def normalize(s: String) = s.trim.toUpperCase
    normalize(code1) == normalize(code2)
  }

  private[impl] def phoneKeyGet()(implicit input: VerifyModel,
                                  p: Pipeline): VerifyResponse = {
    val phoneKey = PhoneKeyFactory().make(KeyId(input.phone))
    phoneKey.getString(CodeProp).flatMap {
      case Some(code) => phoneKeyVerify(phoneKey, code)
      case None       => externalErrorNoCodeResponse
    }
  }

  private[impl] def phoneKeyVerify(phoneKey: HashKey, code: String)(implicit input: VerifyModel,
                                                                    p: Pipeline): VerifyResponse = {
    validateVerifyCodes(input.code, code) match {
      case true  => userIdGet(phoneKey)
      case false => externalErrorCodeValidationResponse
    }
  }

  private[impl] def userIdGet(phoneKey: HashKey)(implicit input: VerifyModel,
                                                 p: Pipeline): VerifyResponse = {
    phoneKey.getId(UserIdProp).flatMap {
      case Some(userId) => userKeyGet(UserKeyFactory().make(KeyId(userId)))
      case None         => internalErrorNoUserIdResponse
    }
  }

  private val userKeyGetProps = Array[Prop](NameProp, GenderProp)
  private[impl] def userKeyGet(userKey: HashKey)(implicit input: VerifyModel,
                                                 p: Pipeline): VerifyResponse = {
    userKey.get(userKeyGetProps).flatMap { props =>
      val name = props.get(NameProp).flatten
      val gender = props.get(GenderProp).flatten
      (name, gender) match {
        case (None, _) if input.name.isEmpty   => externalErrorRequiredParamsResponse
        case (_, None) if input.gender.isEmpty => externalErrorRequiredParamsResponse
        case props =>
          // TODO this is weird get rid of it
          val n = input.name.getOrElse(name.getOrElse(throw new RuntimeException("missing name")))
          val g = input.gender.map(_.toString).getOrElse(gender.getOrElse(throw new RuntimeException("missing gender")))
          userKeySet(userKey, n, g)
      }
    }
  }

  private[impl] def userKeySet(userKey: HashKey, name: String, gender: String)(implicit input: VerifyModel,
                                                                               p: Pipeline): VerifyResponse = {
    val props = Map[Prop, Any](
      NameProp -> name,
      GenderProp -> gender,
      UpdatedProp -> TimeService().asString())
    userKey.set(props).flatMap {
      case true  => deviceKeySet(DeviceKeyFactory().make(KeyId(input.deviceUuid)))
      case false => internalErrorSetUserFailedResponse
    }
  }

  private[impl] def deviceKeySet(deviceKey: HashKey)(implicit input: VerifyModel,
                                                     p: Pipeline): VerifyResponse = {
    deviceKey.setNx(CreatedProp, TimeService().asString()).flatMap { exists =>
      val token = RandomService().uuid
      val props = Map[Prop, Any](
        UpdatedProp -> TimeService().asString(),
        TokenProp -> token)
      deviceKey.set(props).flatMap {
        case true  => VerifyResponseModel(token)
        case false => internalErrorSetDeviceFailedResponse
      }
    }
  }

}

object VerifyServiceImpl {

  private val crudImplicits = CrudImplicits[VerifyResponseModel]
  import base.entity.user.impl.VerifyServiceImpl.crudImplicits._

  lazy val externalErrorNoCode = "A verification code has not been sent for this phone number."
  lazy val externalErrorCodeValidation = "The supplied verification code does not validate."
  lazy val externalErrorRequiredParams = "Name and gender must be supplied upon first verification."
  lazy val externalErrorVerify = "There was a problem with verification."

  lazy val internalErrorNoUserId = "user id is missing"
  lazy val internalErrorSetUserFailed = "failed to set user attributes"
  lazy val internalErrorSetDeviceFailed = "failed to set device attributes"

  lazy val externalErrorNoCodeResponse: VerifyResponse =
    ApiError(externalErrorNoCode, BadRequest, NO_VERIFY_CODE)
  lazy val externalErrorCodeValidationResponse: VerifyResponse =
    ApiError(externalErrorCodeValidation, BadRequest, VERIFY_CODE_VALIDATION)
  lazy val externalErrorRequiredParamsResponse: VerifyResponse =
    ApiError(externalErrorRequiredParams, BadRequest, REQUIRED_PARAMS)
  lazy val internalErrorNoUserIdResponse: VerifyResponse =
    ApiError(externalErrorVerify, InternalServerError, internalErrorNoUserId)
  lazy val internalErrorSetUserFailedResponse: VerifyResponse =
    ApiError(externalErrorVerify, InternalServerError, internalErrorSetUserFailed)
  lazy val internalErrorSetDeviceFailedResponse: VerifyResponse =
    ApiError(externalErrorVerify, InternalServerError, internalErrorSetDeviceFailed)

}

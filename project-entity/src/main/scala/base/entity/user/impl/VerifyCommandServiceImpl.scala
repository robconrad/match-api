/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 9:08 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.lib.Genders.Gender
import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.kv.Key._
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.CrudImplicits
import base.entity.sms.SmsService
import base.entity.user.VerifyCommandService.VerifyResponse
import base.entity.user._
import base.entity.user.impl.VerifyCommandServiceImpl._
import base.entity.user.model._
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class VerifyCommandServiceImpl(codeLength: Int, smsBody: String)
    extends ServiceImpl with VerifyCommandService with Dispatchable with AuthLoggable {

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
    phoneKeyGet(PhoneKeyService().make(KeyId(input.phone)))(input, KvFactoryService().pipeline)
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

  private[impl] def phoneKeyGet(phoneKey: PhoneKey)(implicit input: VerifyModel,
                                                    p: Pipeline): VerifyResponse = {
    phoneKey.getCode.flatMap {
      case Some(code) => phoneKeyVerify(phoneKey, code)
      case None       => externalErrorNoCodeResponse
    }
  }

  private[impl] def phoneKeyVerify(phoneKey: PhoneKey, code: String)(implicit input: VerifyModel,
                                                                     p: Pipeline): VerifyResponse = {
    validateVerifyCodes(input.code, code) match {
      case true  => userIdGet(phoneKey)
      case false => externalErrorCodeValidationResponse
    }
  }

  private[impl] def userIdGet(phoneKey: PhoneKey)(implicit input: VerifyModel,
                                                  p: Pipeline): VerifyResponse = {
    phoneKey.getUserId.flatMap {
      case Some(userId) => userKeyGet(userId, UserKeyService().make(KeyId(userId)))
      case None         => internalErrorNoUserIdResponse
    }
  }

  private[impl] def userKeyGet(userId: UUID, userKey: UserKey)(implicit input: VerifyModel,
                                                               p: Pipeline): VerifyResponse = {
    userKey.getNameAndGender.flatMap {
      case (None, _) if input.name.isEmpty   => externalErrorRequiredParamsResponse
      case (_, None) if input.gender.isEmpty => externalErrorRequiredParamsResponse
      case (name, gender) =>
        // TODO this is weird get rid of it
        val n = input.name.getOrElse(name.getOrElse(throw new RuntimeException("missing name")))
        val g = input.gender.getOrElse(gender.getOrElse(throw new RuntimeException("missing gender")))
        userKeySet(userId, userKey, n, g)
    }
  }

  private[impl] def userKeySet(userId: UUID,
                               userKey: UserKey,
                               name: String,
                               gender: Gender)(implicit input: VerifyModel,
                                               p: Pipeline): VerifyResponse = {
    userKey.setNameAndGender(name, gender).flatMap {
      case true  => deviceKeySet(userId, DeviceKeyService().make(KeyId(input.deviceUuid)))
      case false => internalErrorSetUserFailedResponse
    }
  }

  private[impl] def deviceKeySet(userId: UUID, deviceKey: DeviceKey)(implicit input: VerifyModel,
                                                                     p: Pipeline): VerifyResponse = {
    deviceKey.create.flatMap { exists =>
      val token = RandomService().uuid
      deviceKey.setTokenAndUserId(token, userId).flatMap {
        case true  => VerifyResponseModel(token)
        case false => internalErrorSetDeviceFailedResponse
      }
    }
  }

}

object VerifyCommandServiceImpl {

  private val crudImplicits = CrudImplicits[VerifyResponseModel]
  import base.entity.user.impl.VerifyCommandServiceImpl.crudImplicits._

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

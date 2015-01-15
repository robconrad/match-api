/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:51 AM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.lib.Genders.Gender
import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.{ CrudErrorImplicits, CrudImplicits }
import base.entity.sms.SmsService
import base.entity.user._
import base.entity.user.impl.VerifyCommandServiceImpl.Errors
import base.entity.user.model._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class VerifyCommandServiceImpl(codeLength: Int, smsBody: String)
    extends ServiceImpl
    with VerifyCommandService
    with CrudImplicits[VerifyResponseModel]
    with Dispatchable
    with AuthLoggable {

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
    new VerifyCommand(input).execute()
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

  private[impl] class VerifyCommand(val input: VerifyModel) extends Command[VerifyModel, VerifyResponseModel] {

    def execute() = {
      phoneGetCode(PhoneKeyService().make(KeyId(input.phone)))
    }

    def phoneGetCode(phoneKey: PhoneKey): Response =
      phoneKey.getCode.flatMap {
        case Some(code) => phoneVerify(phoneKey, code)
        case None       => Errors.codeMissing
      }

    def phoneVerify(phoneKey: PhoneKey, code: String): Response =
      validateVerifyCodes(input.code, code) match {
        case true  => phoneGetUserId(phoneKey)
        case false => Errors.codeValidation
      }

    def phoneGetUserId(phoneKey: PhoneKey): Response =
      phoneKey.getUserId.flatMap {
        case Some(userId) => userGet(userId, UserKeyService().make(KeyId(userId)))
        case None         => Errors.userIdMissing
      }

    def userGet(userId: UUID, userKey: UserKey): Response =
      userKey.getNameAndGender.flatMap {
        case (None, _) if input.name.isEmpty   => Errors.paramsMissing
        case (_, None) if input.gender.isEmpty => Errors.paramsMissing
        case (name, gender) =>
          // TODO this is weird get rid of it
          val n = input.name.getOrElse(name.getOrElse(throw new RuntimeException("missing name")))
          val g = input.gender.getOrElse(gender.getOrElse(throw new RuntimeException("missing gender")))
          userSet(userId, userKey, n, g)
      }

    def userSet(userId: UUID,
                userKey: UserKey,
                name: String,
                gender: Gender): Response =
      userKey.setNameAndGender(name, gender).flatMap {
        case true  => deviceSet(userId, DeviceKeyService().make(KeyId(input.deviceUuid)))
        case false => Errors.userSetFailed
      }

    def deviceSet(userId: UUID, deviceKey: DeviceKey): Response =
      deviceKey.create.flatMap { exists =>
        val token = RandomService().uuid
        deviceKey.setTokenAndUserId(token, userId).flatMap {
          case true  => VerifyResponseModel(token)
          case false => Errors.deviceSetFailed
        }
      }

  }

}

object VerifyCommandServiceImpl {

  object Errors extends CrudErrorImplicits[VerifyResponseModel] {

    protected val externalErrorText = "There was a problem with verification."

    private val codeMissingText = "A verification code has not been sent for this phone number."
    private val codeValidationText = "The supplied verification code does not validate."
    private val paramsMissingText = "Name and gender must be supplied upon first verification."

    lazy val codeMissing: Response = (codeMissingText, NO_VERIFY_CODE)
    lazy val codeValidation: Response = (codeValidationText, VERIFY_CODE_VALIDATION)
    lazy val paramsMissing: Response = (paramsMissingText, REQUIRED_PARAMS)
    lazy val userIdMissing: Response = "user id is missing"
    lazy val userSetFailed: Response = "failed to set user attributes"
    lazy val deviceSetFailed: Response = "failed to set device attributes"

  }

}

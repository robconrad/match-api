/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 5:29 PM
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
import base.entity.command.impl.CommandServiceImpl
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.{ CrudErrorImplicits, CrudImplicits }
import base.entity.sms.SmsService
import base.entity.user._
import base.entity.user.impl.VerifyCommandServiceImpl.Errors
import base.entity.user.kv._
import base.entity.user.model._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
class VerifyCommandServiceImpl(codeLength: Int, smsBody: String)
    extends CommandServiceImpl[VerifyModel, VerifyResponseModel]
    with VerifyCommandService {

  def innerExecute(input: VerifyModel)(implicit authCtx: AuthContext) = {
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

  /**
   * - get code
   * - verify code
   * - get userId
   * - get user attributes, reject if none exist and none supplied
   * - store attributes on user
   * - create device
   * - store attributes on device with token
   */
  private[impl] class VerifyCommand(val input: VerifyModel)(implicit val authCtx: AuthContext)
      extends Command[VerifyModel, VerifyResponseModel] {

    def execute() = {
      phoneGetCode(PhoneKeyService().make(KeyId(input.phone)))
    }

    def phoneGetCode(key: PhoneKey): Response =
      key.getCode.flatMap {
        case Some(code) => phoneVerify(key, code)
        case None       => Errors.codeMissing
      }

    def phoneVerify(key: PhoneKey, code: String): Response =
      validateVerifyCodes(input.code, code) match {
        case true  => phoneGetUserId(key)
        case false => Errors.codeValidation
      }

    def phoneGetUserId(key: PhoneKey): Response =
      key.getUserId.flatMap {
        case Some(userId) => userGet(userId, UserKeyService().make(KeyId(userId)))
        case None         => Errors.userIdMissing
      }

    def userGet(userId: UUID, key: UserKey): Response =
      key.getNameAndGender.flatMap {
        case (None, _) if input.name.isEmpty   => Errors.paramsMissing
        case (_, None) if input.gender.isEmpty => Errors.paramsMissing
        case (name, gender) =>
          // TODO this is weird get rid of it
          val n = input.name.getOrElse(name.getOrElse(throw new RuntimeException("missing name")))
          val g = input.gender.getOrElse(gender.getOrElse(throw new RuntimeException("missing gender")))
          userSet(userId, key, n, g)
      }

    def userSet(userId: UUID,
                key: UserKey,
                name: String,
                gender: Gender): Response =
      key.setNameAndGender(name, gender).flatMap {
        case true  => deviceSet(userId, DeviceKeyService().make(KeyId(input.deviceUuid)))
        case false => Errors.userSetFailed
      }

    def deviceSet(userId: UUID, key: DeviceKey): Response =
      key.create.flatMap { exists =>
        val token = RandomService().uuid
        key.setTokenAndUserId(token, userId).flatMap {
          case true  => VerifyResponseModel(token)
          case false => Errors.deviceSetFailed
        }
      }

  }

}

object VerifyCommandServiceImpl {

  object Errors extends CrudErrorImplicits[VerifyResponseModel] {

    override protected val externalErrorText = "There was a problem with verification."

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

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:15 AM
 */

package base.entity.user.impl

import base.common.random.RandomService
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.service.CrudErrorImplicits
import base.entity.sms.SmsService
import base.entity.user._
import base.entity.user.impl.VerifyPhoneCommandServiceImpl.Errors
import base.entity.user.kv._
import base.entity.user.model._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
class VerifyPhoneCommandServiceImpl(codeLength: Int, smsBody: String)
    extends CommandServiceImpl[VerifyPhoneModel, VerifyPhoneResponseModel]
    with VerifyPhoneCommandService {

  override protected val responseManifest = Option(manifest[VerifyPhoneResponseModel])

  def innerExecute(input: VerifyPhoneModel)(implicit channelCtx: ChannelContext) = {
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
   * - set phone verified
   */
  private[impl] class VerifyCommand(val input: VerifyPhoneModel)(implicit val channelCtx: ChannelContext)
      extends Command[VerifyPhoneModel, VerifyPhoneResponseModel] {

    def execute() = {
      phoneGetCode(PhoneKeyService().make(input.phone))
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
        case Some(userId) => userSet(UserKeyService().make(userId))
        case None         => Errors.userIdMissing
      }

    def userSet(key: UserKey): Response =
      key.setPhoneVerified(input.phone, verified = true).flatMap {
        case true  => VerifyPhoneResponseModel(input.phone)
        case false => Errors.userSetFailed
      }

  }

}

object VerifyPhoneCommandServiceImpl {

  object Errors extends CrudErrorImplicits[VerifyPhoneResponseModel] {

    override protected val externalErrorText = "There was a problem with verification."

    private val codeMissingText = "A verification code has not been sent for this phone number."
    private val codeValidationText = "The supplied verification code does not validate."

    lazy val codeMissing: Response = (codeMissingText, NO_VERIFY_CODE)
    lazy val codeValidation: Response = (codeValidationText, VERIFY_CODE_VALIDATION)
    lazy val userIdMissing: Response = "user id is missing"
    lazy val userSetFailed: Response = "failed to set user attributes"

  }

}

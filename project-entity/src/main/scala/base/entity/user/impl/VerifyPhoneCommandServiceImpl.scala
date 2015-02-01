/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 4:28 PM
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
   * - get user phone attributes
   * - verify code
   * - set user phone verified
   * - set userId to phone
   * - transfer invites from phone to user
   */
  private[impl] class VerifyCommand(val input: VerifyPhoneModel)(implicit val channelCtx: ChannelContext)
      extends Command[VerifyPhoneModel, VerifyPhoneResponseModel] {

    def execute() = {
      userGetPhoneAttributes(UserKeyService().make(authCtx.userId))
    }

    def userGetPhoneAttributes(key: UserKey): Response =
      key.getPhoneAttributes.flatMap {
        case Some(attributes) => userVerifyPhoneCode(key, attributes.code)
        case None             => Errors.codeMissing
      }

    def userVerifyPhoneCode(key: UserKey, code: String): Response =
      validateVerifyCodes(input.code, code) match {
        case true  => userSetPhoneVerified(key)
        case false => Errors.codeValidation
      }

    def userSetPhoneVerified(key: UserKey): Response =
      key.setPhoneVerified(verified = true) flatMap {
        case true  => phoneSetUserId(PhoneKeyService().make(input.phone))
        case false => Errors.userSetPhoneVerifiedFailed
      }

    def phoneSetUserId(key: PhoneKey): Response =
      key.set(authCtx.userId).flatMap {
        case true =>
          val phoneInvitedKey = PhoneGroupsInvitedKeyService().make(input.phone)
          val userInvitedKey = UserGroupsInvitedKeyService().make(authCtx.userId)
          phoneInvitesToUserInvites(phoneInvitedKey, userInvitedKey)
        case false => Errors.phoneSetUserIdFailed
      }

    def phoneInvitesToUserInvites(phoneInvitedKey: PhoneGroupsInvitedKey,
                                  userInvitedKey: UserGroupsInvitedKey): Response =
      PhoneGroupsInvitedKeyService().unionStore(userInvitedKey, userInvitedKey, phoneInvitedKey) flatMap { response =>
        phoneInvitedKey.del() flatMap { response =>
          VerifyPhoneResponseModel(input.phone)
        }
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
    lazy val userSetPhoneVerifiedFailed: Response = "failed to set user phone verified"
    lazy val phoneSetUserIdFailed: Response = "failed to set phone user id"

  }

}

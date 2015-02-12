/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:24 PM
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

  private val base = 10
  private val codeMin = Math.pow(base, codeLength - 1).toInt
  private val codeMax = Math.pow(base, codeLength).toInt

  def innerExecute(input: VerifyPhoneModel)(implicit channelCtx: ChannelContext) = {
    new VerifyCommand(input).execute()
  }

  def sendVerifySms(phone: String, code: String) = {
    SmsService().send(phone, smsBody.format(code))
  }

  def makeVerifyCode() = {
    RandomService().int(codeMin, codeMax).toString
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
      userGetPhoneAttributes(make[UserKey](authCtx.userId))
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
      key.setPhoneVerified(verified = true) flatMap { result =>
        phoneSetUserId(PhoneKeyService().make(input.phone))
      }

    def phoneSetUserId(key: PhoneKey): Response =
      key.set(authCtx.userId).flatMap {
        case true =>
          val phoneInvitedKey = make[PhoneGroupsInvitedKey](input.phone)
          val userInvitedKey = make[UserGroupsInvitedKey](authCtx.userId)
          phoneInvitesToUserInvites(phoneInvitedKey, userInvitedKey)
        case false => Errors.phoneSetUserIdFailed
      }

    def phoneInvitesToUserInvites(phoneInvitedKey: PhoneGroupsInvitedKey,
                                  userInvitedKey: UserGroupsInvitedKey): Response =
      userInvitedKey.unionStore(userInvitedKey, phoneInvitedKey) flatMap { response =>
        phoneInvitedKey.del() flatMap { response =>
          userGetInvitesIn(UserService())
        }
      }

    def userGetInvitesIn(service: UserService): Response =
      service.getPendingGroups(authCtx.userId) flatMap {
        case Right(invitesIn) => VerifyPhoneResponseModel(input.phone, invitesIn)
        case Left(error)      => error
      }

  }

}

object VerifyPhoneCommandServiceImpl {

  object Errors extends CrudErrorImplicits[VerifyPhoneResponseModel] {

    override protected val externalErrorText = "There was a problem with verification."

    private val codeMissingText = "A verification code has not been sent for this phone number."
    private val codeValidationText = "The supplied verification code does not validate."

    lazy val codeMissing: Response = (codeMissingText, VERIFY_CODE_MISSING)
    lazy val codeValidation: Response = (codeValidationText, VERIFY_CODE_INVALID)
    lazy val phoneSetUserIdFailed: Response = "failed to set phone user id"

  }

}

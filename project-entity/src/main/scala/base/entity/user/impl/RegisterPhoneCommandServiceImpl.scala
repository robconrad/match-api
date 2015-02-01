/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.entity.user.impl

import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.error.model.ApiError
import base.entity.service.CrudErrorImplicits
import base.entity.user._
import base.entity.user.impl.RegisterPhoneCommandServiceImpl.Errors
import base.entity.user.kv._
import base.entity.user.model.{ RegisterPhoneModel, RegisterPhoneResponseModel }
import spray.http.StatusCodes

import scala.concurrent.duration.FiniteDuration

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class RegisterPhoneCommandServiceImpl(phoneCooldown: FiniteDuration)
    extends CommandServiceImpl[RegisterPhoneModel, RegisterPhoneResponseModel]
    with RegisterPhoneCommandService {

  override protected val responseManifest = Option(manifest[RegisterPhoneResponseModel])

  def innerExecute(input: RegisterPhoneModel)(implicit channelCtx: ChannelContext) = {
    new RegisterCommand(input).execute()
  }

  /**
   * - reject if phone in cooldown
   * - set phone cooldown
   * - expire phone cooldown
   * - set user phone attributes
   * - send SMS verification
   */
  private[impl] class RegisterCommand(val input: RegisterPhoneModel)(implicit val channelCtx: ChannelContext)
      extends Command[RegisterPhoneModel, RegisterPhoneResponseModel] {

    def execute() = {
      phoneCooldownExists(PhoneCooldownKeyService().make(input.phone))
    }

    def phoneCooldownExists(key: PhoneCooldownKey): Response =
      key.exists().flatMap {
        case false => phoneCooldownSet(key)
        case true  => Errors.phoneCooldown
      }

    def phoneCooldownSet(key: PhoneCooldownKey): Response =
      key.set(RegisterPhoneCommandServiceImpl.phoneCooldownValue).flatMap {
        case true  => phoneCooldownExpire(key)
        case false => Errors.phoneCooldownSetFailed
      }

    def phoneCooldownExpire(key: PhoneCooldownKey): Response =
      key.expire(phoneCooldown.toSeconds).flatMap {
        case true  => userSetPhoneAttributes(UserKeyService().make(authCtx.userId))
        case false => Errors.phoneCooldownExpireFailed
      }

    def userSetPhoneAttributes(key: UserKey): Response = {
      val code = VerifyPhoneCommandService().makeVerifyCode()
      info("phone verification code for %s is %s", input.phone, code)
      key.setPhoneAttributes(UserPhoneAttributes(input.phone, code, verified = false)) flatMap {
        case true  => smsSend(code)
        case false => Errors.userSetFailed
      }
    }

    def smsSend(code: String): Response =
      VerifyPhoneCommandService().sendVerifySms(input.phone, code).map {
        case true  => RegisterPhoneResponseModel(input.phone)
        case false => Errors.smsSendFailed
      }

  }

}

object RegisterPhoneCommandServiceImpl {

  val phoneCooldownValue = 1

  object Errors extends CrudErrorImplicits[RegisterPhoneResponseModel] {

    override protected val externalErrorText = "There was a problem with registration."

    private val phoneCooldownText = "This phone number has been registered too recently."

    lazy val phoneCooldown: Response = (phoneCooldownText, StatusCodes.EnhanceYourCalm, PHONE_RATE_LIMIT)
    lazy val phoneCooldownSetFailed: Response = "failed to set phoneCooldown"
    lazy val phoneCooldownExpireFailed: Response = "failed to expire phoneCooldown"
    lazy val userSetFailed: Response = "failed to set phone attributes on user"
    lazy val smsSendFailed: ApiError = "failed to send sms"

  }

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:19 PM
 */

package base.entity.group.impl

import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.error.ApiError
import base.entity.group.InviteCommandService
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.service.CrudErrorImplicits

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class InviteCommandServiceImpl(smsBody: String)
    extends CommandServiceImpl[InviteModel, InviteResponseModel]
    with InviteCommandService {

  def innerExecute(input: InviteModel)(implicit authCtx: AuthContext) = {
    new InviteCommand(input).execute()
  }

  /**
   * - check if group exists
   * - get invited userId from phone
   * - sms invited user if not exists
   * - create invited user if not exists
   * - create group
   */
  private[impl] class InviteCommand(val input: InviteModel) extends Command[InviteModel, InviteResponseModel] {

    def execute() = {
      ApiError("")
    }

    //    def phoneGetUserId(key: PhoneKey) =
    //      key.getUserId.flatMap {
    //        case Some(userId) => userGet(UserKeyService().make(KeyId(userId)))
    //        case None => sendInviteSms()
    //      }
    //
    //    def userGet(key: UserKey) =
    //
    //       //groupCreate(GroupKeyService().make(KeyId(userId)))
    //    def sendInviteSms() =
    //      SmsService().send(input.phone, smsBody) match {
    //        case true => userCreate(PhoneKeyService().make(KeyId(input.phone)))
    //        case false => Errors.smsSendFailed
    //      }
    //
    //    def userCreate(key: PhoneKey): Response = {
    //      val userId = RandomService().uuid
    //      UserKeyService().make(KeyId(userId)).create().flatMap {
    //        case false => Errors.userSetFailed
    //        case true =>
    //          key.setUserId(userId).flatMap {
    //            case true => groupCreate()
    //            case false => Errors.userSetFailed
    //          }
    //      }
    //    }
    //
    //    def groupCreate(key: GroupKey) =

  }

}

object InviteCommandServiceImpl {

  object Errors extends CrudErrorImplicits[InviteResponseModel] {

    protected val externalErrorText = "There was a problem during invite."

    lazy val userSetFailed: Response = "failed to create user"
    lazy val smsSendFailed: Response = "failed to send sms"

  }

}

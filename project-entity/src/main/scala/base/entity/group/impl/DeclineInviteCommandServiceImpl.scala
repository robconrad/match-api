/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:26 PM
 */

package base.entity.group.impl

import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.group._
import base.entity.group.impl.DeclineInviteCommandServiceImpl.Errors
import base.entity.group.model.{ DeclineInviteModel, DeclineInviteResponseModel }
import base.entity.service.CrudErrorImplicits
import base.entity.user.kv.UserGroupsInvitedKey

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class DeclineInviteCommandServiceImpl()
    extends CommandServiceImpl[DeclineInviteModel, DeclineInviteResponseModel]
    with DeclineInviteCommandService {

  override protected val responseManifest = Option(manifest[DeclineInviteResponseModel])

  def innerExecute(input: DeclineInviteModel)(implicit channelCtx: ChannelContext) = {
    new DeclineInviteCommand(input).execute()
  }

  /**
   * - remove invite
   */
  private[impl] class DeclineInviteCommand(val input: DeclineInviteModel)(implicit val channelCtx: ChannelContext)
      extends Command[DeclineInviteModel, DeclineInviteResponseModel] {

    def execute() = {
      userGroupsInvitedRemove(make[UserGroupsInvitedKey](authCtx.userId))
    }

    def userGroupsInvitedRemove(key: UserGroupsInvitedKey) =
      key.rem(input.groupId) flatMap {
        case 1L => DeclineInviteResponseModel(input.groupId)
        case _  => Errors.userGroupsInvitedRemoveFailed
      }

  }

}

object DeclineInviteCommandServiceImpl {

  object Errors extends CrudErrorImplicits[DeclineInviteResponseModel] {

    override protected val externalErrorText = "There was a problem during decline invite."

    lazy val userGroupsInvitedRemoveFailed: Response = "failed to remove invite from user groups invited"

  }

}

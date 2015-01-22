/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:54 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.impl.InviteCommandServiceImpl.Errors
import base.entity.group.kv._
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.group.{ GroupEventsService, GroupService, InviteCommandService }
import base.entity.service.CrudErrorImplicits
import base.entity.user.kv._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class InviteCommandServiceImpl(welcomeMessage: String)
    extends CommandServiceImpl[InviteModel, InviteResponseModel]
    with InviteCommandService {

  def innerExecute(input: InviteModel)(implicit authCtx: AuthContext) = {
    new InviteCommand(input).execute()
  }

  /**
   * - get invited userId from phone
   * - create invited user if not exists
   * - set phone user id if new user
   * - create invited user label
   * - check if group pair exists
   * - create group
   * - create group pair
   * - create group users
   * - update user groups
   * - add welcome message to group events
   * - get and return group
   */
  private[impl] class InviteCommand(val input: InviteModel)(implicit val authCtx: AuthContext)
      extends Command[InviteModel, InviteResponseModel] {

    def execute() = {
      phoneGetUserId(PhoneKeyService().make(input.phone))
    }

    def phoneGetUserId(key: PhoneKey) =
      key.create().flatMap { exists =>
        key.getUserId.flatMap {
          case Some(userId) => userUserLabelSet(userId, UserUserLabelKeyService().make(authCtx.userId, userId))
          case None =>
            val userId = RandomService().uuid
            userCreate(userId, UserKeyService().make(userId), key)
        }
      }

    def userCreate(userId: UUID, userKey: UserKey, phoneKey: PhoneKey) =
      userKey.create().flatMap {
        case true  => phoneSetUserId(userId, phoneKey)
        case false => Errors.userCreateFailed
      }

    def phoneSetUserId(userId: UUID, key: PhoneKey) =
      key.setUserId(userId).flatMap {
        case true  => userUserLabelSet(userId, UserUserLabelKeyService().make(authCtx.userId, userId))
        case false => Errors.phoneSetUserIdFailed
      }

    def userUserLabelSet(userId: UUID, key: UserUserLabelKey) =
      key.set(input.label).flatMap {
        case true  => groupPairGet(userId, GroupPairKeyService().make(userId, authCtx.userId))
        case false => Errors.userUserLabelSetFailed
      }

    def groupPairGet(userId: UUID, key: GroupPairKey) =
      key.get.flatMap {
        case Some(groupId) => groupGet(userId, groupId)
        case None =>
          val groupId = RandomService().uuid
          groupPairSet(userId, groupId, GroupKeyService().make(groupId), key)
      }

    def groupPairSet(userId: UUID, groupId: UUID, groupKey: GroupKey, pairKey: GroupPairKey) =
      groupKey.create().flatMap { exists =>
        pairKey.set(groupId).flatMap {
          case true  => groupUsersAdd(userId, groupId, GroupUsersKeyService().make(groupId))
          case false => Errors.pairSetFailed
        }
      }

    def groupUsersAdd(userId: UUID, groupId: UUID, key: GroupUsersKey) =
      key.add(userId, authCtx.userId).flatMap {
        case 2 => invitedUserGroupsAdd(userId, groupId, UserGroupsKeyService().make(userId))
        case _ => Errors.groupUsersAddFailed
      }

    def invitedUserGroupsAdd(userId: UUID, groupId: UUID, key: UserGroupsKey) =
      key.add(groupId).flatMap {
        case 1 => invitingUserGroupsAdd(userId, groupId, UserGroupsKeyService().make(authCtx.userId))
        case _ => Errors.userGroupsAddFailed
      }

    def invitingUserGroupsAdd(userId: UUID, groupId: UUID, key: UserGroupsKey) =
      key.add(groupId).flatMap {
        case 1 => groupEventsPrepend(userId, groupId)
        case _ => Errors.userGroupsAddFailed
      }

    def groupEventsPrepend(userId: UUID, groupId: UUID) = {
      val event = EventModel(
        id = RandomService().uuid,
        groupId = groupId,
        `type` = EventTypes.MESSAGE,
        body = welcomeMessage)
      GroupEventsService().setEvent(event, createIfNotExists = true).flatMap {
        case Right(event) => groupGet(userId, groupId)
        case Left(error)  => error
      }
    }

    def groupGet(userId: UUID, groupId: UUID) =
      GroupService().getGroup(groupId).flatMap {
        case Right(Some(group)) => InviteResponseModel(userId, group)
        case Right(None)        => Errors.groupGetFailed
        case Left(error)        => error
      }

  }

}

object InviteCommandServiceImpl {

  object Errors extends CrudErrorImplicits[InviteResponseModel] {

    override protected val externalErrorText = "There was a problem during invite."

    lazy val userCreateFailed: Response = "failed to create user"
    lazy val phoneSetUserIdFailed: Response = "failed to set user id on phone"
    lazy val userUserLabelSetFailed: Response = "failed to set userUserLabel"
    lazy val groupGetFailed: Response = "failed to get group"
    lazy val pairSetFailed: Response = "failed to set group pair"
    lazy val groupUsersAddFailed: Response = "failed to add to groupUsers"
    lazy val userGroupsAddFailed: Response = "failed to add to userGroups"

  }

}

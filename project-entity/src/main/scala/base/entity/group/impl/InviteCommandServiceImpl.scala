/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 6:06 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.group.impl.InviteCommandServiceImpl.Errors
import base.entity.group.kv._
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.group.{ GroupService, InviteCommandService }
import base.entity.kv.KeyId
import base.entity.service.CrudErrorImplicits
import base.entity.user.kv._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class InviteCommandServiceImpl()
    extends CommandServiceImpl[InviteModel, InviteResponseModel]
    with InviteCommandService {

  def innerExecute(input: InviteModel)(implicit authCtx: AuthContext) = {
    new InviteCommand(input).execute()
  }

  /**
   * - get invited userId from phone
   * - create invited user if not exists
   * - check if group pair exists
   * - create group
   * - create group users
   * - update user groups
   * - create group pair
   */
  private[impl] class InviteCommand(val input: InviteModel)(implicit val authCtx: AuthContext)
      extends Command[InviteModel, InviteResponseModel] {

    def execute() = {
      phoneGetUserId(PhoneKeyService().make(KeyId(input.phone)))
    }

    def phoneGetUserId(key: PhoneKey) =
      key.create().flatMap { exists =>
        key.getUserId.flatMap {
          case Some(userId) => groupPairGet(userId, GroupPairKeyService().make(userId, authCtx.userId))
          case None =>
            val userId = RandomService().uuid
            userCreate(userId, UserKeyService().make(KeyId(userId)), key)
        }
      }

    def userCreate(userId: UUID, userKey: UserKey, phoneKey: PhoneKey) = {
      userKey.create().flatMap {
        case true  => phoneSetUserId(userId, phoneKey)
        case false => Errors.userCreateFailed
      }
    }

    def phoneSetUserId(userId: UUID, key: PhoneKey) = {
      key.setUserId(userId).flatMap {
        case true  => groupPairGet(userId, GroupPairKeyService().make(userId, authCtx.userId))
        case false => Errors.phoneSetUserIdFailed
      }
    }

    def groupPairGet(userId: UUID, key: GroupPairKey) =
      key.get.flatMap {
        case Some(groupId) => groupGet(userId, groupId)
        case None =>
          val groupId = RandomService().uuid
          groupPairSet(userId, groupId, GroupKeyService().make(KeyId(groupId)), key)
      }

    def groupPairSet(userId: UUID, groupId: UUID, groupKey: GroupKey, pairKey: GroupPairKey) =
      groupKey.create().flatMap { exists =>
        pairKey.set(groupId).flatMap {
          case true  => groupUsersAdd(userId, groupId, GroupUsersKeyService().make(KeyId(groupId)))
          case false => Errors.pairSetFailed
        }
      }

    def groupUsersAdd(userId: UUID, groupId: UUID, key: GroupUsersKey) =
      key.add(userId, authCtx.userId).flatMap {
        case 2 => invitedUserGroupsAdd(userId, groupId, UserGroupsKeyService().make(KeyId(userId)))
        case _ => Errors.groupUsersAddFailed
      }

    def invitedUserGroupsAdd(userId: UUID, groupId: UUID, key: UserGroupsKey) =
      key.add(groupId).flatMap {
        case 1 => invitingUserGroupsAdd(userId, groupId, UserGroupsKeyService().make(KeyId(authCtx.userId)))
        case _ => Errors.userGroupsAddFailed
      }

    def invitingUserGroupsAdd(userId: UUID, groupId: UUID, key: UserGroupsKey) =
      key.add(groupId).flatMap {
        case 1 => groupGet(userId, groupId)
        case _ => Errors.userGroupsAddFailed
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

    protected val externalErrorText = "There was a problem during invite."

    lazy val userCreateFailed: Response = "failed to create user"
    lazy val phoneSetUserIdFailed: Response = "failed to set user id on phone"
    lazy val groupGetFailed: Response = "failed to get group"
    lazy val pairSetFailed: Response = "failed to set group pair"
    lazy val groupUsersAddFailed: Response = "failed to add to groupUsers"
    lazy val userGroupsAddFailed: Response = "failed to add to userGroups"

  }

}

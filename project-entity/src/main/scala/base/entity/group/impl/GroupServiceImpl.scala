/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 9:55 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.group.kv._
import base.entity.group.model.{ GroupModel, GroupModelBuilder }
import base.entity.group.{ GroupService, UserService }
import base.entity.kv.Key.Pipeline
import base.entity.service.CrudImplicits

/**
 * {{ Describe the high level purpose of GroupServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupServiceImpl extends ServiceImpl with GroupService {

  def getGroup(userId: UUID, groupId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    new GetGroupMethod(userId, groupId).execute()
  }

  private[impl] class GetGroupMethod(userId: UUID, groupId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext)
      extends CrudImplicits[Option[GroupModel]] {

    def execute(): Response = {
      val key = GroupUserKeyService().make(groupId, userId)
      groupUserGetSetLastRead(key, GroupModelBuilder(id = Option(groupId)))
    }

    def groupUserGetSetLastRead(key: GroupUserKey, builder: GroupModelBuilder): Response =
      key.getLastRead.flatMap { lastRead =>
        val key = GroupKeyService().make(groupId)
        groupGet(key, builder.copy(lastReadTime = lastRead))
      }

    def groupGet(key: GroupKey, builder: GroupModelBuilder): Response =
      key.getLastEventAndCount.flatMap {
        case (lastEvent, count) =>
          val key = GroupUsersKeyService().make(groupId)
          groupUsersGet(key, builder.copy(lastEventTime = lastEvent, eventCount = Option(count.getOrElse(0))))
      }

    def groupUsersGet(key: GroupUsersKey, builder: GroupModelBuilder): Response =
      key.members().flatMap { userIds =>
        usersGet(userIds.toList, builder)
      }

    def usersGet(userIds: List[UUID], builder: GroupModelBuilder): Response = {
      UserService().getUsers(userId, userIds).map {
        case Right(users) => Option(builder.copy(users = Option(users)).build)
        case Left(error)  => error
      }
    }

  }

}

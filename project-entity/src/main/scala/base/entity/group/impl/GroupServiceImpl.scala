/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:02 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.group.GroupService
import base.entity.group.kv._
import base.entity.group.model.impl.{ GroupModelBuilder, InviteModelImpl }
import base.entity.group.model.{ GroupModel, InviteModel }
import base.entity.kv.Key.Pipeline
import base.entity.kv.MakeKey
import base.entity.service.CrudImplicits
import base.entity.user.UserService
import base.entity.user.kv.{ PhoneKeyService, UserPhone, UserPhoneLabelKeyService }

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupServiceImpl extends ServiceImpl with GroupService with MakeKey {

  def getGroup(userId: UUID, groupId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    new GetGroupMethod(userId, groupId).execute()
  }

  private[impl] class GetGroupMethod(userId: UUID, groupId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext)
      extends CrudImplicits[Option[GroupModel]] {

    def execute(): Response = {
      val key = make[GroupUserKey](groupId, userId)
      groupUserGetSetLastRead(key, GroupModelBuilder(id = Option(groupId)))
    }

    def groupUserGetSetLastRead(key: GroupUserKey, builder: GroupModelBuilder): Response =
      key.getLastRead.flatMap { lastRead =>
        val key = make[GroupKey](groupId)
        groupGet(key, builder.copy(lastReadTime = Option(lastRead)))
      }

    def groupGet(key: GroupKey, builder: GroupModelBuilder): Response =
      key.getLastEventAndCount.flatMap {
        case (lastEvent, count) =>
          val key = make[GroupUsersKey](groupId)
          groupUsersGet(key, builder.copy(lastEventTime = Option(lastEvent), eventCount = Option(count.getOrElse(0))))
      }

    def groupUsersGet(key: GroupUsersKey, builder: GroupModelBuilder): Response =
      key.members.flatMap { userIds =>
        usersGet(userIds.toList, builder)
      }

    def usersGet(userIds: List[UUID], builder: GroupModelBuilder): Response = {
      UserService().getUsers(userId, userIds).flatMap {
        case Left(error) => error
        case Right(users) =>
          groupPhonesInvitedGet(make[GroupPhonesInvitedKey](groupId), builder.copy(users = Option(users)))
      }
    }

    def groupPhonesInvitedGet(key: GroupPhonesInvitedKey, builder: GroupModelBuilder): Response = {
      key.members flatMap { phones =>
        val userIds = phones.map { phone =>
          PhoneKeyService().make(phone).get.map(userId => phone -> userId)
        }
        phoneUsersGet(Future.sequence(userIds), builder)
      }
    }

    def phoneUsersGet(userIds: Future[Set[(String, Option[UUID])]], builder: GroupModelBuilder): Response = {
      userIds flatMap { userIdOpts =>
        val userIds = userIdOpts.collect { case (phone, Some(userId)) => userId }
        val phones = userIdOpts.collect { case (phone, None) => phone }

        UserService().getUsers(userId, userIds.toList) map {
          case Right(users) =>
            users.map { user =>
              userIdOpts.find(_._2.contains(user.id)).map(_._1 -> user)
            }.collect {
              case Some((phone, user)) => phone -> InviteModelImpl(phone, user.pictureUrl, user.name)
            }.toMap[String, InviteModel]
          case Left(apiError) =>
            error("received error but continuing anyway, %s", apiError)
            Map[String, InviteModel]()
        } flatMap { users =>
          phoneLabelsGet(phones, users, builder)
        }
      }
    }

    def phoneLabelsGet(phones: Set[String], users: Map[String, InviteModel], builder: GroupModelBuilder): Response = {
      val phoneLabels = phones.filter(!users.isDefinedAt(_)).map { phone =>
        val key = UserPhoneLabelKeyService().make(UserPhone(userId, phone))
        key.get.map(label => phone -> InviteModelImpl(phone, None, label))
      }
      Future.sequence(phoneLabels) map { phoneLabels =>
        val invites = phoneLabels.toMap ++ users
        Right(Option(builder.copy(invites = Option(invites.values.toList)).build))
      }
    }

  }

}

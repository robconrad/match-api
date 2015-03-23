/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 7:07 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.group.GroupService
import base.entity.group.kv._
import base.entity.group.model.impl.{ GroupModelBuilder, InviteModelImpl }
import base.entity.group.model.{ GroupModel, InviteModel }
import base.entity.kv.MakeKey
import base.entity.service.CrudImplicits
import base.entity.user.UserService
import base.entity.user.kv.{ PhoneKey, UserPhone, UserPhoneLabelKey }

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupServiceImpl extends ServiceImpl with GroupService with MakeKey {

  def getGroup(userId: UUID, groupId: UUID, hydrate: Boolean)(implicit channelCtx: ChannelContext) = {
    new GetGroupMethod(userId, groupId, hydrate).execute()
  }

  private[impl] class GetGroupMethod(userId: UUID, groupId: UUID, hydrate: Boolean)(implicit channelCtx: ChannelContext)
      extends CrudImplicits[Option[GroupModel]] {

    def execute(): Response = {
      val key = make[GroupUserKey](groupId, userId)
      groupUserGetLastRead(key, GroupModelBuilder(id = Option(groupId)))
    }

    def groupUserGetLastRead(key: GroupUserKey, builder: GroupModelBuilder): Response =
      key.getLastReadEventCount.flatMap { lastRead =>
        val key = make[GroupKey](groupId)
        groupGet(key, builder.copy(lastReadEventCount = Option(lastRead)))
      }

    def groupGet(key: GroupKey, builder: GroupModelBuilder): Response =
      key.getLastEventAndCount.flatMap {
        case (lastEvent, count) =>
          val updatedBuilder = builder.copy(eventCount = Option(count.getOrElse(0)))
          // when hydration is not requested we don't bother executing the expensive task of getting users and invites
          hydrate match {
            case true =>
              val key = make[GroupUsersKey](groupId)
              groupUsersGet(key, updatedBuilder)
            case false =>
              Right(Option(updatedBuilder.copy(users = Option(None), invites = Option(None)).build))
          }
      }

    def groupUsersGet(key: GroupUsersKey, builder: GroupModelBuilder): Response =
      key.members.flatMap { userIds =>
        usersGet(userIds.toList, builder)
      }

    def usersGet(userIds: List[UUID], builder: GroupModelBuilder): Response = {
      UserService().getUsers(userId, userIds).flatMap {
        case Left(error) => error
        case Right(users) =>
          groupPhonesInvitedGet(make[GroupPhonesInvitedKey](groupId), builder.copy(users = Option(Option(users))))
      }
    }

    def groupPhonesInvitedGet(key: GroupPhonesInvitedKey, builder: GroupModelBuilder): Response = {
      key.members flatMap { phones =>
        val userIds = phones.map { phone =>
          make[PhoneKey](phone).get.map(userId => phone -> userId)
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
        val key = make[UserPhoneLabelKey](UserPhone(userId, phone))
        key.get.map(label => phone -> InviteModelImpl(phone, None, label))
      }
      Future.sequence(phoneLabels) map { phoneLabels =>
        val invites = phoneLabels.toMap ++ users
        Right(Option(builder.copy(invites = Option(Option(invites.values.toList))).build))
      }
    }

  }

}

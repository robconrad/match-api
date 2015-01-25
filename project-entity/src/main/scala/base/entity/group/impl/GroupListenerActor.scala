/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:12 AM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.lib.BaseActor
import base.entity.auth.context.PushChannel
import base.entity.command.model.CommandModel
import base.entity.event.model.EventModel
import base.entity.group.impl.GroupListenerActor.{ Publish, Register, Unregister }

import scala.collection.mutable
import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupListenerActor here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupListenerActor extends BaseActor {

  private val groupUsers = mutable.Map[UUID, Set[UUID]]()
  private val userGroups = mutable.Map[UUID, Set[UUID]]()
  private val userChannels = mutable.Map[UUID, PushChannel]()

  def receive = {
    case msg: Register   => register(msg)
    case msg: Unregister => unregister(msg)
    case msg: Publish    => publish(msg)
    case msg             => processUnexpectedMessage(msg)
  }

  def register(msg: Register) {
    import msg._
    groupIds foreach { groupId =>
      groupUsers get groupId match {
        case Some(users) => groupUsers put (groupId, users + userId)
        case None        => groupUsers put (groupId, Set(userId))
      }
    }
    userGroups get userId match {
      case Some(groups) => userGroups put (userId, groups ++ groupIds)
      case None         => userGroups put (userId, groupIds)
    }
    userChannels put (userId, pushChannel)
    sender ! Unit
  }

  def unregister(msg: Unregister) {
    import msg._
    userGroups remove userId foreach { groupIds =>
      groupIds foreach { groupId =>
        groupUsers(groupId) - userId match {
          case users if users.size == 0 => groupUsers remove groupId
          case users                    => groupUsers put (groupId, users)
        }
      }
    }
    userChannels remove userId
    sender ! Unit
  }

  def publish(publish: Publish) {
    groupUsers get publish.command.body.groupId match {
      case Some(userIds) =>
        val futures = userIds.map { userId =>
          userChannels get userId map { channel =>
            // The reason we future this off is that we can't have expensive operations tying up this actor
            //  since it's a choke point for the whole app. Who knows what's happening inside publish!
            //  (json serialization for one)
            Future {
              channel push publish.command
            } flatMap identity
          }
        }.collect {
          case Some(future) => future
        }
        val s = sender()
        Future.sequence(futures) foreach { f =>
          s ! Unit
        }
      case None => sender ! Unit
    }
  }

}

object GroupListenerActor {

  case class Register(groupIds: Set[UUID], userId: UUID, pushChannel: PushChannel)
  case class Unregister(userId: UUID)
  case class Publish(command: CommandModel[EventModel])

}

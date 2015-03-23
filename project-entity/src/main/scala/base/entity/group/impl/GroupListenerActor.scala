/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 8:34 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.lib.BaseActor
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.auth.context.{ ChannelContext, NoAuthContext, PushChannel }
import base.entity.command.model.CommandModel
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.GroupService
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
    debug("register %s", msg)
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
    debug("unregister %s", msg)
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

  def publish(msg: Publish) {
    debug("publish %s", msg)
    groupUsers get msg.command.body.groupId match {
      case Some(userIds) =>
        val futures = userIds.map { userId =>
          userChannels get userId map { channel =>
            hydrateGroup(userId, msg.command) flatMap {
              case Some(command) =>
                try {
                  debug(s"channel push $command")
                  channel push command
                } catch {
                  case e: Throwable =>
                    error("channel push threw ", e)
                    throw e
                }
              case None => Future.successful(false)
            }
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

  private implicit val channelCtx: ChannelContext = ChannelContextImpl(NoAuthContext, None)
  private def hydrateGroup(userId: UUID, command: CommandModel[EventModel]) = {
    val event = command.body
    GroupService().getGroup(userId, command.body.groupId) map {
      case Right(Some(group)) =>
        val newEvent = EventModelImpl(
          event.id,
          Option(group),
          event.groupId,
          userId = event.userId,
          `type` = event.`type`,
          body = event.body,
          time = event.time)
        Option(command.copy(body = newEvent))
      case Right(None) =>
        error("no group found")
        None
      case Left(e) =>
        error(e.toString)
        None
    }
  }

}

object GroupListenerActor {

  case class Register(groupIds: Set[UUID], userId: UUID, pushChannel: PushChannel)
  case class Unregister(userId: UUID)
  case class Publish(command: CommandModel[EventModel])

}

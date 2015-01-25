/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 11:23 PM
 */

package base.entity.group.impl

import java.util.UUID

import akka.actor.{ ActorRef, Props }
import akka.pattern.ask
import base.common.lib.Actors
import base.common.service.{ CommonService, ServiceImpl }
import base.entity.auth.context.ChannelContext
import base.entity.command.model.CommandModel
import base.entity.event.model.EventModel
import base.entity.group.GroupListenerService
import base.entity.group.impl.GroupListenerActor.{ Publish, Register, Unregister }
import base.entity.logging.AuthLoggable

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupListenerServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupListenerServiceImpl(actor: ActorRef) extends ServiceImpl with GroupListenerService with AuthLoggable {

  private implicit val timeout = CommonService().defaultTimeout

  def register(userId: UUID, groupIds: Set[UUID])(implicit channelCtx: ChannelContext) = {
    debug("register %s", groupIds)
    channelCtx.pushChannel.map { pushChannel =>
      actor ? Register(groupIds, userId, pushChannel) map (x => Unit)
    }.getOrElse(Future.successful(Unit))
  }

  def unregister(userId: UUID)(implicit channelCtx: ChannelContext) = {
    debug("unregister")
    actor ? Unregister(userId) map (x => Unit)
  }

  def publish(event: CommandModel[EventModel])(implicit channelCtx: ChannelContext) = {
    debug("publish %s", event)
    actor ? Publish(event) map (x => Unit)
  }

}

object GroupListenerServiceImpl {

  def makeActor = Actors.actorSystem.actorOf(Props(new GroupListenerActor()), "group-listener")

}

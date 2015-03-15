/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:16 AM
 */

package base.socket.api

import base.common.service.{ Service, ServiceCompanion }
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ ChannelFuture, ChannelHandlerContext, ChannelInboundHandler, ChannelInitializer }

import scala.concurrent.duration.FiniteDuration

/**
 *
 * @author rconrad
 */
trait SocketApiHandlerService extends Service with ChannelInboundHandler {

  final val serviceManifest = manifest[SocketApiHandlerService]

  def makeInitializer(idleTimeout: FiniteDuration): ChannelInitializer[SocketChannel]

  def stop()

  def write(json: String)(implicit ctx: ChannelHandlerContext): ChannelFuture

}

object SocketApiHandlerService extends ServiceCompanion[SocketApiHandlerService] {

}

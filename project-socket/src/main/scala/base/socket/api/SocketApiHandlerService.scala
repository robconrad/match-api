/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 6:14 PM
 */

package base.socket.api

import base.common.service.{ Service, ServiceCompanion }
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ ChannelHandlerContext, ChannelFuture, ChannelInitializer, ChannelInboundHandler }

/**
 *
 * @author rconrad
 */
trait SocketApiHandlerService extends Service with ChannelInboundHandler {

  final val serviceManifest = manifest[SocketApiHandlerService]

  def makeInitializer: ChannelInitializer[SocketChannel]

  def stop()

  def write(json: String)(implicit ctx: ChannelHandlerContext): ChannelFuture

}

object SocketApiHandlerService extends ServiceCompanion[SocketApiHandlerService] {

}

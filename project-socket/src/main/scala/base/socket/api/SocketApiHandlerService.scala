/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:58 PM
 */

package base.socket.api

import base.common.service.{ Service, ServiceCompanion }
import io.netty.channel.ChannelInboundHandler

/**
 *
 * @author rconrad
 */
trait SocketApiHandlerService extends Service with ChannelInboundHandler {

  final val serviceManifest = manifest[SocketApiHandlerService]

  def stop()

}

object SocketApiHandlerService extends ServiceCompanion[SocketApiHandlerService] {

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 10:45 AM
 */

package base.socket.api

import base.common.service.{ Service, ServiceCompanion }
import io.netty.channel.ChannelInboundHandler

/**
 *
 * @author rconrad
 */
trait SocketApiHandlerService extends Service with ChannelInboundHandler {

  final def serviceManifest = manifest[SocketApiHandlerService]

  def stop()

}

object SocketApiHandlerService extends ServiceCompanion[SocketApiHandlerService] {

}

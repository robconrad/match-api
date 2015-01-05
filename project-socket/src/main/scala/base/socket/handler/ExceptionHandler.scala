/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 2:41 PM
 */

package base.socket.handler

import io.netty.channel.ChannelHandlerContext

/**
 * Basic Netty Exception-handling with Logging capabilities.
 */
object ExceptionHandler {
  /**
   * Bad client = closed connection, malformed requests, etc.
   *
   * Do nothing if the exception is one of the following:
   * java.io.IOException: Connection reset by peer
   * java.io.IOException: Broken pipe
   * java.nio.channels.ClosedChannelException: null
   * javax.net.ssl.SSLException: not an SSL/TLS record (Use http://... URL to connect to HTTPS server)
   * java.lang.IllegalArgumentException: empty text (Use http://... URL to connect to HTTPS server)
   */
  def apply(ctx: ChannelHandlerContext, cause: Throwable): Option[Throwable] = {
    val s = cause.toString
    if (s.startsWith("java.nio.channels.ClosedChannelException") ||
      s.startsWith("java.io.IOException") ||
      s.startsWith("javax.net.ssl.SSLException") ||
      s.startsWith("java.lang.IllegalArgumentException")) {
      None
    } else {
      if (ctx.channel.isOpen) ctx.channel.closeFuture()
      Some(cause)
    }
  }
}

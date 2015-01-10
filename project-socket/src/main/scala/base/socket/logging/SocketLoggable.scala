/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 12:59 PM
 */

package base.socket.logging

import base.common.logging.Loggable
import base.socket._
import io.netty.channel.{ Channel, ChannelHandlerContext }

/**
 * {{ Describe the high level purpose of SocketLoggable here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SocketLoggable extends Loggable {

  protected implicit def ctx2Channel(ctx: ChannelHandlerContext): Channel = ctx.channel
  protected implicit def ctx2Loggable(ctx: ChannelHandlerContext): LoggableChannelInfo = ctx.channel

  protected def trace(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isTraceEnabled) super.trace(format(msg, x: _*))
  }

  protected def debug(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isDebugEnabled) super.debug(format(msg, x: _*))
  }

  protected def info(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isInfoEnabled) super.info(format(msg, x: _*))
  }

  protected def warn(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isWarnEnabled) super.warn(format(msg, x: _*))
  }
  protected def warn(msg: String, e: Throwable, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isWarnEnabled) super.warn(format(msg, x: _*), e)
  }

  protected def error(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isErrorEnabled) super.error(format(msg, x: _*))
  }
  protected def error(msg: String, e: Throwable, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isErrorEnabled) super.error(format(msg, x: _*), e)
  }

  private def format(msg: String, x: Any*)(implicit ch: LoggableChannelInfo): String =
    super.format(s"User:${ch.authCtx.map(_.user.map(_.id)).flatten} Addr:${ch.remoteAddress} $msg", x: _*)

}

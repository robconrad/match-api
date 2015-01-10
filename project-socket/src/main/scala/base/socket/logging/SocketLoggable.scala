/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:54 PM
 */

package base.socket.logging

import base.common.logging.Loggable

/**
 * {{ Describe the high level purpose of SocketLoggable here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SocketLoggable extends Loggable {

  protected def trace(ch: LoggableChannelInfo, msg: String, x: Any*) {
    if (isTraceEnabled) trace(format(ch, msg, x: _*))
  }

  protected def debug(ch: LoggableChannelInfo, msg: String, x: Any*) {
    if (isDebugEnabled) debug(format(ch, msg, x: _*))
  }

  protected def info(ch: LoggableChannelInfo, msg: String, x: Any*) {
    if (isInfoEnabled) info(format(ch, msg, x: _*))
  }

  protected def warn(ch: LoggableChannelInfo, msg: String, x: Any*) {
    if (isWarnEnabled) warn(format(ch, msg, x: _*))
  }
  protected def warn(ch: LoggableChannelInfo, msg: String, e: Throwable, x: Any*) {
    if (isWarnEnabled) warn(format(ch, msg, x: _*), e)
  }

  protected def error(ch: LoggableChannelInfo, msg: String, x: Any*) {
    if (isErrorEnabled) error(format(ch, msg, x: _*))
  }
  protected def error(ch: LoggableChannelInfo, msg: String, e: Throwable, x: Any*) {
    if (isErrorEnabled) error(format(ch, msg, x: _*), e)
  }

  private def format(ch: LoggableChannelInfo, msg: String, x: Any*): String =
    format(s"User:${ch.authCtx.map(_.user.map(_.id)).flatten} Addr:${ch.remoteAddress} $msg", x: _*)

}

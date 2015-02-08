/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 10:28 PM
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

  private def prependAuthCtx(x: Seq[Any])(implicit ch: LoggableChannelInfo) =
    Seq(ch.authCtx.user.map(_.id), ch.remoteAddress) ++ x

  private def prependFormat(msg: String): String = "User:%s Addr:%s " + msg

  protected def trace(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isTraceEnabled) super.trace(prependFormat(msg), prependAuthCtx(x): _*)
  }

  protected def debug(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isDebugEnabled) super.debug(prependFormat(msg), prependAuthCtx(x): _*)
  }

  protected def info(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isInfoEnabled) super.info(prependFormat(msg), prependAuthCtx(x): _*)
  }

  protected def warn(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isWarnEnabled) super.warn(prependFormat(msg), prependAuthCtx(x): _*)
  }
  protected def warn(msg: String, e: Throwable, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isWarnEnabled) super.warn(prependFormat(msg), e, prependAuthCtx(x): _*)
  }

  protected def error(msg: String, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isErrorEnabled) super.error(prependFormat(msg), prependAuthCtx(x): _*)
  }
  protected def error(msg: String, e: Throwable, x: Any*)(implicit ch: LoggableChannelInfo) {
    if (isErrorEnabled) super.error(prependFormat(msg), e, prependAuthCtx(x): _*)
  }

}

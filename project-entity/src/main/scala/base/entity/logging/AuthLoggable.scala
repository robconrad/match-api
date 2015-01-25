/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:45 PM
 */

package base.entity.logging

import base.common.logging.TokenLoggable
import base.entity.auth.context.ChannelContext

/**
 * Mixin to allow classes to write to default log and capture any auth context in scope
 *  NB: to make this work there has to be an AuthContext in scope!
 * @author rconrad
 */
trait AuthLoggable extends TokenLoggable {

  def authCtx(implicit channelCtx: ChannelContext) = channelCtx.authCtx

  private def prependAuthCtx(x: Seq[Any])(implicit channelCtx: ChannelContext) = x.+:(authCtx.toLogPrefix)

  /**
   * Prints a message on trace.
   */
  protected def trace(msg: String, x: Any*)(implicit channelCtx: ChannelContext) {
    super.trace("%s: " + msg, prependAuthCtx(x): _*)(authCtx.token)
  }

  /**
   * Prints a message on debug.
   */
  protected def debug(msg: String, x: Any*)(implicit channelCtx: ChannelContext) {
    super.debug("%s: " + msg, prependAuthCtx(x): _*)(authCtx.token)
  }

  /**
   * Prints a message on info.
   */
  protected def info(msg: String, x: Any*)(implicit channelCtx: ChannelContext) {
    super.info("%s: " + msg, prependAuthCtx(x): _*)(authCtx.token)
  }

  /**
   * Prints a message on warn.
   */
  protected def warn(msg: String, e: Throwable, x: Any*)(implicit channelCtx: ChannelContext) {
    super.warn("%s: " + msg, e, prependAuthCtx(x): _*)(authCtx.token)
  }
  protected def warn(msg: String, x: Any*)(implicit channelCtx: ChannelContext) {
    super.warn("%s: " + msg, prependAuthCtx(x): _*)(authCtx.token)
  }

  /**
   * Prints a message on error.
   */
  protected def error(msg: String, e: Throwable, x: Any*)(implicit channelCtx: ChannelContext) {
    super.error("%s: " + msg, e, prependAuthCtx(x): _*)(authCtx.token)
  }
  protected def error(msg: String, x: Any*)(implicit channelCtx: ChannelContext) {
    super.error("%s: " + msg, prependAuthCtx(x): _*)(authCtx.token)
  }

}

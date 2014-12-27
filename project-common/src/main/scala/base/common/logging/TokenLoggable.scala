/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.logging

/**
 * Mixin to allow classes to write to default log with token support
 *  Meaning every log entry will be tied to the implicitly in scope token which may identify
 *  a particular user, actor, or other long- or short-lived process.
 * @author rconrad
 */
trait TokenLoggable extends Loggable {

  private def prependAuthCtx(x: Seq[Any])(implicit token: LoggerToken) = x.+:(token.s)

  protected def token = LoggerToken()

  /**
   * Prints a message on debug.
   */
  protected def debug(msg: String, x: Any*)(implicit token: LoggerToken) {
    super.debug("%s: " + msg, prependAuthCtx(x): _*)
  }

  /**
   * Prints a message on info.
   */
  protected def info(msg: String, x: Any*)(implicit token: LoggerToken) {
    super.info("%s: " + msg, prependAuthCtx(x): _*)
  }

  /**
   * Prints a message on warn.
   */
  protected def warn(msg: String, e: Throwable, x: Any*)(implicit token: LoggerToken) {
    super.warn("%s: " + msg, e, prependAuthCtx(x): _*)
  }
  protected def warn(msg: String, x: Any*)(implicit token: LoggerToken) {
    super.warn("%s: " + msg, prependAuthCtx(x): _*)
  }

  /**
   * Prints a message on error.
   */
  protected def error(msg: String, e: Throwable, x: Any*)(implicit token: LoggerToken) {
    super.error("%s: " + msg, e, prependAuthCtx(x): _*)
  }
  protected def error(msg: String, x: Any*)(implicit token: LoggerToken) {
    super.error("%s: " + msg, prependAuthCtx(x): _*)
  }

}

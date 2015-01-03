/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/2/15 2:51 PM
 */

package base.common.logging

import java.io.{ PrintWriter, StringWriter }

import org.slf4j.LoggerFactory

/**
 * Mixin to allow classes to write to default log
 * @author rconrad
 */
trait Loggable {

  /**
   * Override this to give your class a custom Loggable name.
   */
  protected def loggerName = this.getClass.getSimpleName
  private final lazy val logger = LoggerFactory.getLogger(loggerName)

  /**
   * Queries whether the mode is enabled
   */
  protected def isTraceEnabled = logger.isTraceEnabled
  protected def isDebugEnabled = logger.isDebugEnabled
  protected def isInfoEnabled = logger.isInfoEnabled
  protected def isWarnEnabled = logger.isWarnEnabled
  protected def isErrorEnabled = logger.isErrorEnabled

  /**
   * Prints a message on debug.
   */
  protected def trace(msg: String, x: Any*) {
    if (isTraceEnabled) logger.trace(format(msg, x: _*))
  }

  /**
   * Prints a message on debug.
   */
  protected def debug(msg: String, x: Any*) {
    if (isDebugEnabled) logger.debug(format(msg, x: _*))
  }

  /**
   * Prints a message on info.
   */
  protected def info(msg: String, x: Any*) {
    if (isInfoEnabled) logger.info(format(msg, x: _*))
  }

  /**
   * Prints a message on warn.
   */
  protected def warn(msg: String, e: Throwable, x: Any*) {
    if (isWarnEnabled) logger.warn(format(msg, x: _*), e)
  }
  protected def warn(msg: String, x: Any*) {
    if (isWarnEnabled) logger.warn(format(msg, x: _*))
  }

  /**
   * Prints a message on error.
   */
  protected def error(msg: String, e: Throwable, x: Any*) {
    if (isErrorEnabled) logger.error(format(msg, x: _*), e)
  }
  protected def error(msg: String, x: Any*) {
    if (isErrorEnabled) logger.error(format(msg, x: _*))
  }

  /**
   * The reason we do this instead of passing the args down into the logger to let it do format is two-fold:
   *  1. This allows format overriding for special loggers that want to apply extra parameters
   *  2. The scala compiler can't understand "x: _*" across the java interface, gives this error:
   *       no `: _*` annotation allowed here (such annotations are only allowed in arguments to *-parameters)
   */
  protected def format(msg: String, x: Any*): String = x.size match {
    case 0 => msg
    case _ => msg.format(x: _*)
  }

}

object Loggable {

  def stackTraceToString(throwable: Throwable) = {
    val w = new StringWriter
    throwable.printStackTrace(new PrintWriter(w))
    w.toString
  }

}

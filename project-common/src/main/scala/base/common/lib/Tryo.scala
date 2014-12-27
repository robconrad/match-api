/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.lib

import scala.util.Success

/**
 * Convenient utility for specifying default behavior on option emptiness or exception throwiness
 * @author rconrad
 */
object Tryo {
  def apply[T](f: => T): Option[T] = scala.util.Try(f) match { case Success(x) => Some(x) case _ => None }
  def apply[T](f: => T, fallback: T): T = scala.util.Try(f) getOrElse fallback
}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 12:22 PM
 */

package base.common.lib

object Ifo {
  def apply[T](v: T, test: Boolean): Option[T] = test match { case true => Some(v) case false => None }
}

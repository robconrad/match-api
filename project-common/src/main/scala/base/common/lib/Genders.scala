/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:50 PM
 */

package base.common.lib

/**
 * {{ Describe the high level purpose of Gender here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object Genders extends Enumeration {
  type Gender = Value

  implicit def asString(c: Gender) = c.toString

  val male = Value
  val female = Value
  val other = Value

}

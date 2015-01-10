/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:59 PM
 */

package base.entity.perm

/**
 * Permissions to access particular areas of functionality in the API.
 * @author rconrad
 */
object Perms extends Enumeration {
  type Perm = Value

  val REGISTER = Value
  val VERIFY = Value
  val LOGIN = Value
  val INVITE = Value
  val MESSAGE = Value
  val ANSWER = Value
  val QUESTION = Value

}

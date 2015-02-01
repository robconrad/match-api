/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 12:01 PM
 */

package base.entity.perm

/**
 * Permissions to access particular areas of functionality in the API.
 * @author rconrad
 */
object Perms extends Enumeration {
  type Perm = Value

  val LOGIN = Value
  val REGISTER_PHONE = Value
  val VERIFY_PHONE = Value
  val INVITE = Value
  val ACCEPT_INVITE = Value
  val DECLINE_INVITE = Value
  val MESSAGE = Value
  val ANSWER = Value
  val QUESTIONS = Value

}

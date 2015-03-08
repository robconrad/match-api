/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/7/15 4:55 PM
 */

package base.entity.api

/**
 * Possible error codes used to disambiguate 400 responses
 * @author rconrad
 */
object ApiErrorCodes extends Enumeration {
  type ErrorCode = Value

  val TEST = Value

  val JSON_NOT_FOUND = Value
  val JSON_COMMAND_NOT_FOUND = Value
  val JSON_BODY_NOT_FOUND = Value
  val COMMAND_NOT_HANDLED = Value

  val NOT_GROUP_MEMBER = Value
  
  val ALREADY_LOGGED_IN = Value

  val PHONE_RATE_LIMIT = Value
  val VERIFY_CODE_MISSING = Value
  val VERIFY_CODE_INVALID = Value
  val TOKEN_INVALID = Value
  val INVITED_SELF = Value
  val INVITED_ALREADY = Value
  val ANSWERED_ALREADY = Value
  val SERVER_NOT_RUNNING = Value
  val SERVER_BUSY = Value

}

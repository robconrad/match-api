/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 9:23 AM
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

  val PHONE_RATE_LIMIT = Value
  val NO_VERIFY_CODE = Value
  val VERIFY_CODE_VALIDATION = Value
  val TOKEN_NOT_VALID = Value
  val ALREADY_INVITED = Value
  val SERVER_NOT_RUNNING = Value
  val SERVER_BUSY = Value

}

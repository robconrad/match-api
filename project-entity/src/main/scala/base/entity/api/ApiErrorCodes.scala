/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 10:45 AM
 */

package base.entity.api

/**
 * Possible error codes used to disambiguate 400 responses
 * @author rconrad
 */
object ApiErrorCodes extends Enumeration {
  type ErrorCode = Value

  val TEST = Value

  val PHONE_RATE_LIMIT = Value
  val NO_VERIFY_CODE = Value
  val VERIFY_CODE_VALIDATION = Value
  val DEVICE_NOT_VERIFIED = Value
  val TOKEN_NOT_VALID = Value
  val REQUIRED_PARAMS = Value
  val SERVER_NOT_RUNNING = Value
  val SERVER_BUSY = Value

}

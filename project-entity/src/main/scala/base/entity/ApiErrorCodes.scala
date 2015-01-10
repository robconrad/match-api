/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 10:22 AM
 */

package base.entity

/**
 * Possible error codes used to disambiguate 400 responses
 * @author rconrad
 */
object ApiErrorCodes extends Enumeration {
  type ErrorCode = Value

  val TEST = Value

}

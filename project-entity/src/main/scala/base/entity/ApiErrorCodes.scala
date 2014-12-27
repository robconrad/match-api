/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity

/**
 * Possible error codes used to disambiguate 400 responses
 * @author rconrad
 */
object ApiErrorCodes extends Enumeration {
  type ErrorCode = Value

  val DUPE_ORDER_IDENTIFIER = Value("duplicate_order_identifier")

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.test

/**
 * Exceptions thrown only by Test code
 * @author rconrad
 */
object TestExceptions {

  class TestRuntimeException(s: String) extends RuntimeException(s: String)

}

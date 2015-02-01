/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 3:03 PM
 */

package base.common.test

/**
 * Exceptions thrown only by Test code
 * @author rconrad
 */
object TestExceptions {

  class TestRuntimeException(s: String = "") extends RuntimeException(s: String)

}

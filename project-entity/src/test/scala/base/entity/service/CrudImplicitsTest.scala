/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 12:07 PM
 */

package base.entity.service

import base.entity.error.ApiError
import base.entity.test.EntityBaseSuite
import spray.http.StatusCodes

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of CrudImplicitsTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CrudImplicitsTest extends EntityBaseSuite {

  type Result = Either[ApiError, Int]

  test("int implicits") {
    val foo = CrudImplicits[Int]
    import foo._

    val value = 1
    val error = "api error"
    val status = StatusCodes.Forbidden
    val seed = "error seed"

    val right: Result = value
    assert(right == Right(value))

    val apiErrorWithStatus: Result = (error, status)
    assert(apiErrorWithStatus == Left(ApiError(error, status)))

    val apiErrorWithSeed: Result = (error, status, seed)
    assert(apiErrorWithSeed == Left(ApiError(error, status, seed)))

    val apiError: Result = ApiError(error)
    assert(apiError == Left(ApiError(error)))

    val future: Future[Result] = value
    assert(future.await() == Right(value))
  }

}

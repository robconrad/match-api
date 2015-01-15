/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:11 AM
 */

package base.entity.service

import base.entity.api.ApiErrorCodes
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

  private def assertImplicits[T](value: T)(implicit m: Manifest[T]) {
    type Result = Either[ApiError, T]

    object DoAsserts extends CrudImplicits[T] {

      private val error = "api error"
      private val status = StatusCodes.Forbidden
      private val seed = "error seed"
      private val code = ApiErrorCodes.TEST

      private val tuple2ApiError: Result = (error, status)
      assert(tuple2ApiError == Left(ApiError(error, status)))

      private val stringTuple2ApiError: Result = (error, status, seed)
      assert(stringTuple2ApiError == Left(ApiError(error, status, seed)))

      private val codeTuple2ApiError: Result = (error, status, code)
      assert(codeTuple2ApiError == Left(ApiError(error, status, code)))

      private val result2Right: Result = value
      assert(result2Right == Right(value))

      private val apiError2Left: Result = ApiError(error)
      assert(apiError2Left == Left(ApiError(error)))

      private val either2Future: Future[Result] = Right[ApiError, T](value)
      assert(either2Future.await() == Right(value))

    }

    DoAsserts
  }

  test("Int implicits") {
    assertImplicits(1)
    assertImplicits(2)
  }

  test("String implicits") {
    assertImplicits("foo")
    assertImplicits("bar")
  }

}

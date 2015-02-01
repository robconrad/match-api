/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 8:59 AM
 */

package base.entity.service

import base.entity.api.ApiErrorCodes
import base.entity.error.{ApiError, ApiErrorService}
import base.entity.test.EntityBaseSuite
import spray.http.StatusCodes

/**
 * {{ Describe the high level purpose of CrudImplicitsTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CrudErrorImplicitsTest extends EntityBaseSuite {

  private def assertImplicits[T](value: T)(implicit m: Manifest[T]) {
    type Result = Either[ApiError, T]

    object DoAsserts extends CrudErrorImplicits[T] {

      override protected val externalErrorText = "external api error"

      private val error = "api error"
      private val code = ApiErrorCodes.TEST

      private val externalTuple2ApiError: Result = (error, code)
      assert(externalTuple2ApiError ==
        Left(ApiErrorService().errorCode(error, StatusCodes.BadRequest, code)))

      private val internalTuple2ApiError: Result = error
      assert(internalTuple2ApiError ==
        Left(ApiErrorService().statusCodeSeed(externalErrorText, StatusCodes.InternalServerError, error)))

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

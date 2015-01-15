/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:43 AM
 */

package base.entity.service

import base.entity.api.ApiErrorCodes.ErrorCode
import base.entity.error.ApiError
import spray.http.StatusCode

import scala.concurrent.Future

/**
 * Provides generic helper methods for services that do CRUD against the database. Useful for making
 *  concise return values and removing the boilerplate of the wrapper classes (Either, ApiError, etc)
 * @author rconrad
 */
private[entity] trait CrudImplicits[T] {

  type Response = Future[Either[ApiError, T]]

  /**
   * Convenience implicits for wrapping responses in Either[] and formatting EntityError
   */
  protected implicit def tuple2ApiError(e: (String, StatusCode)) =
    ApiError(e._1, e._2)

  protected implicit def stringTuple2ApiError(e: (String, StatusCode, String)) =
    ApiError(e._1, e._2, e._3)

  protected implicit def codeTuple2ApiError(e: (String, StatusCode, ErrorCode)) =
    ApiError(e._1, e._2, e._3)

  protected implicit def result2Right[A <% T](r: A): Either[ApiError, T] =
    Right(r)

  protected implicit def apiError2Left[A <% ApiError](e: A): Either[ApiError, T] =
    Left(e)

  protected implicit def either2Future[A <% Either[ApiError, T]](e: A): Future[Either[ApiError, T]] =
    Future.successful(e)

}

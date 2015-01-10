/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:24 PM
 */

package base.entity.service

import base.common.lib.Dispatchable
import base.entity.error.ApiError
import spray.http.StatusCode

import scala.concurrent.Future

/**
 * Provides generic helper methods for services that do CRUD against the database. Useful for making
 *  concise return values and removing the boilerplate of the wrapper classes (Either, EntityError, etc)
 * @author rconrad
 */
private[entity] trait CrudServiceImplHelper[T] extends Dispatchable {

  /**
   * Convenience implicits for wrapping responses in Either[] and formatting EntityError
   */
  protected implicit def result2Right(r: T): Either[ApiError, T] =
    Right(r)

  protected implicit def string2ApiError(e: (String, StatusCode)): Either[ApiError, T] =
    string2ApiError(e._1, e._2, e._1)

  protected implicit def string2ApiError(e: (String, StatusCode, String)): Either[ApiError, T] =
    ApiError(e._1, e._2, e._3)

  protected implicit def apiError2Left(e: ApiError): Either[ApiError, T] =
    Left(e)

  protected implicit def either2Future[E <% Either[ApiError, T]](e: E): Future[Either[ApiError, T]] =
    Future.successful(e)

}

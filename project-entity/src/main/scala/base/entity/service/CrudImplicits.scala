/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 12:07 PM
 */

package base.entity.service

import base.common.lib.Dispatchable
import base.common.service.Service
import base.entity.error.ApiError
import spray.http.StatusCode

import scala.concurrent.Future
import scala.reflect.Manifest

/**
 * Provides generic helper methods for services that do CRUD against the database. Useful for making
 *  concise return values and removing the boilerplate of the wrapper classes (Either, ApiError, etc)
 * @author rconrad
 */
private[entity] class CrudImplicits[T] extends Dispatchable {

  /**
   * Convenience implicits for wrapping responses in Either[] and formatting EntityError
   */
  implicit def result2Right(r: T): Either[ApiError, T] =
    Right(r)

  implicit def string2ApiError(e: (String, StatusCode)): Either[ApiError, T] =
    ApiError(e._1, e._2)

  implicit def string2ApiError(e: (String, StatusCode, String)): Either[ApiError, T] =
    ApiError(e._1, e._2, e._3)

  implicit def apiError2Left(e: ApiError): Either[ApiError, T] =
    Left(e)

  implicit def either2Future[E <% Either[ApiError, T]](e: E): Future[Either[ApiError, T]] =
    Future.successful(e)

}

object CrudImplicits {

  private var map = Map.empty[Manifest[_], CrudImplicits[_]]

  def apply[T](implicit m: Manifest[T]): CrudImplicits[T] = {
    map.get(m) match {
      case Some(c) => c.asInstanceOf[CrudImplicits[T]]
      case None =>
        val c = new CrudImplicits[T]
        map = map.updated(m, c)
        c
    }
  }

}

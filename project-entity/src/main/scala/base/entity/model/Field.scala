/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.model

import base.entity.error.ApiError
import spray.http.StatusCodes

import scala.slick.lifted.MappedTo

/**
 * A strongly typed field allows us to maintain type safety all the way from database to API response. By including
 *  json serializers and slick serializers we can avoid "stringly" typed field (and same for ints, doubles, etc.)
 * @author rconrad
 */
private[model] trait Field[T] extends MappedTo[T] {

  /**
   * The primary purpose of a Field is to contain a Value v
   */
  val v: T

  /**
   * Frequently field validation functions must return errors, these implicits allow those errors to be
   *  bare API Strings since a validation error almost invariably results in a BadRequest ApiError
   */
  implicit def string2Error(s: String) = ApiError(s, StatusCodes.BadRequest)
  implicit def error2Option(e: ApiError) = Some(e)

  /**
   * Field companions provide convenience implicits and json serialization, this is primarily here
   *  to ensure a companion is created for each field in development
   */
  val companion: FieldCompanion[T, _]

  /**
   * Fields must be "validateable" i.e., there must be some criteria by which we can determine if they are valid
   *  (like length, size, modality, etc.)
   */
  def validate: Option[ApiError]

  /**
   * Maps the the field's primary value to the $value expected by the Slick Mapper we extend
   */
  def value = v

  /**
   * Special equality tester allows convenient access to value of the field (most useful in test scenarios)
   */
  def ===(v: T) = this.v == v

  /**
   * Fields always resolve to their value's string representation
   */
  final override def toString = v.toString

}


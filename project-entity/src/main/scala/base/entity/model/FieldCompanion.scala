/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.model

import org.json4s.CustomSerializer

/**
 * Companion objects for fields provide serialization and convenience implicits
 */
private[model] trait FieldCompanion[T, F <: Field[T]] {

  /**
   * Conveniently converse between Field and the raw type of $v
   */
  implicit def optionType(v: T): Option[F] = Option(forwardsType(v))
  implicit def optionForwardsType(v: Option[T]): Option[F] = v.map(forwardsType)
  implicit def optionBackwardsType(v: Option[F]): Option[T] = v.map(backwardsType)
  implicit def forwardsType(v: T): F
  implicit def backwardsType(v: F): T

  /**
   * Field companions must define a json serializer for communication with requests
   */
  def serializer: CustomSerializer[_]

}

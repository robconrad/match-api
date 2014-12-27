/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.model

import base.entity.error.ApiError

/**
 * Base type for string fields provides simple length validation only
 */
private[entity] trait StringField extends Field[String] {

  val minLength: Int
  val maxLength: Int
  val lengthError: ApiError

  def validate =
    value.length < minLength || value.length > maxLength match {
      case true  => lengthError
      case false => None
    }

}

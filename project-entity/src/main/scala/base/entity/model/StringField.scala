/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.entity.model

import base.entity.error.model.ApiError

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

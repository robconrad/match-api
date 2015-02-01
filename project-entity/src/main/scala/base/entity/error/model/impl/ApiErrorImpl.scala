/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 11:01 AM
 */

package base.entity.error.model.impl

import base.entity.api.ApiErrorCodes.ErrorCode
import base.entity.error.model.ApiError
import spray.http.StatusCode

/**
 * {{ Describe the high level purpose of ApiErrorImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class ApiErrorImpl private[error] (
  command: Option[String],
  status: StatusCode,
  code: Option[ErrorCode],
  param: Option[String],
  message: String,
  uniqueId: String,
  debug: Option[String]) extends ApiError

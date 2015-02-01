/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.entity.service

import base.entity.api.ApiErrorCodes.ErrorCode
import base.entity.error.model.ApiError
import spray.http.StatusCodes._

/**
 * {{ Describe the high level purpose of CrudErrors here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] trait CrudErrorImplicits[T] extends CrudImplicits[T] {

  protected def externalErrorText = "A server error occurred."

  protected implicit def externalTuple2ApiError(e: (String, ErrorCode)): ApiError =
    (e._1, BadRequest, e._2)

  protected implicit def internalTuple2ApiError(error: String): ApiError =
    (externalErrorText, InternalServerError, error)

}

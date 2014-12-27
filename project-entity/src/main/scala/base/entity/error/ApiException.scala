/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.error

import spray.http.{ StatusCode, StatusCodes }

/**
 * Base level exception that can be thrown by API code with the expectation that the caller will handle
 *  it as an expected runtime exception. Specifically HTTP API callers may use the status field to set
 *  the HTTP return code of a response from a request that generated such an exception.
 * @author rconrad
 */
class ApiException(val msg: String,
                   val t: Option[Throwable] = None,
                   val status: StatusCode = StatusCodes.InternalServerError) extends RuntimeException(msg, t.orNull)

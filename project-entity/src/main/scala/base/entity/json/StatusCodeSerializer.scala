/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 10:55 AM
 */

package base.entity.json

import base.entity.error.ApiException
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JInt
import spray.http.{ StatusCode, StatusCodes }

/**
 * Allows Spray HttpData to be written to json during request response logging
 * @author rconrad
 */
case object StatusCodeSerializer extends CustomSerializer[StatusCode](format => (

  {
    case code: JInt =>
      StatusCodes.getForKey(code.num.intValue())
        .getOrElse(throw new ApiException(s"status code $code is not valid", status = StatusCodes.BadRequest))
  },
  {
    case code: StatusCode => JInt(code.intValue)
  }

))


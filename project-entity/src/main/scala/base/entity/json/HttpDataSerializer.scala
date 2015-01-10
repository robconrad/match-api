/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 11:31 AM
 */

package base.entity.json

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString
import spray.http.HttpData

/**
 * Allows Spray HttpData to be written to json during request response logging
 * @author rconrad
 */
case object HttpDataSerializer extends CustomSerializer[HttpData](format => (

  { case json: JString => HttpData(json.s) },
  { case d: HttpData => JString(d.asString) }

))

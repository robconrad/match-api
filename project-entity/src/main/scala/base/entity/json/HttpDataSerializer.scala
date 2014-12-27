/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.json

import org.json4s.CustomSerializer
import org.json4s.native.JsonMethods
import spray.http.HttpData

/**
 * Allows Spray HttpData to be written to json during request response logging
 * @author rconrad
 */
case object HttpDataSerializer extends CustomSerializer[HttpData](format => (

  { case json => HttpData(json.toString) },
  { case d: HttpData => JsonMethods.parse(d.asString) }

))

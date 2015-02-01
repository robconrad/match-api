/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 12:38 PM
 */

package base.entity.json

import base.common.time.TimeService
import org.joda.time.DateTime
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

case object DateTimeSerializer extends CustomSerializer[DateTime](format => ( {
  case JString(s) => TimeService().fromString(s)
}, {
  case d: DateTime => JString(TimeService().asString(d))
}))

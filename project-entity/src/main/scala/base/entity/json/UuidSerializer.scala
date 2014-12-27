/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.json

import java.util.UUID

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{ JNull, JString }

// scalastyle:off null
case object UuidSerializer extends CustomSerializer[UUID](format => ({
  case JString(s) => UUID.fromString(s)
  case JNull      => null
}, {
  case x: UUID => JString(x.toString)
}))
// scalastyle:on

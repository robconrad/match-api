/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 2:42 PM
 */

package base.entity.model

import base.entity.api.ApiStrings
import base.entity.error.ApiError
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

/**
 * Identifier field usable from DB to API layer, provides validation, etc.
 * @author rconrad
 */
case class Identifier(v: String) extends StringField {

  val minLength = Identifier.LENGTH_MIN
  val maxLength = Identifier.LENGTH_MAX
  lazy val lengthError: ApiError = ApiStrings.Field.identifierLengthErrorDesc

  val companion = Identifier

}

object Identifier extends FieldCompanion[String, Identifier] {

  implicit def forwardsType(v: String) = Identifier(v)
  implicit def backwardsType(v: Identifier) = v.v

  val serializer = new CustomSerializer[Identifier](format => ({
    case JString(s) => Identifier(s)
  }, {
    case x: Identifier => JString(x.toString)
  }))

  // NOTE: these values are used in ApiStrings.Site and must be updated
  //  there if changed here (strings there are const only so no interpolation)
  val LENGTH_MIN = 0
  val LENGTH_MAX = 255

}

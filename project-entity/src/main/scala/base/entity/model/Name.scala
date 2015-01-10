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
 * Name field usable from DB to API layer, provides validation, etc.
 * @author rconrad
 */
case class Name(v: String) extends StringField {

  val minLength = Name.LENGTH_MIN
  val maxLength = Name.LENGTH_MAX
  lazy val lengthError: ApiError = ApiStrings.Field.nameLengthErrorDesc

  val companion = Name

}

object Name extends FieldCompanion[String, Name] {

  implicit def forwardsType(v: String) = Name(v)
  implicit def backwardsType(v: Name) = v.v

  val serializer = new CustomSerializer[Name](format => ({
    case JString(s) => Name(s)
  }, {
    case x: Name => JString(x.toString)
  }))

  // NOTE: these values are used in ApiStrings.Site and must be updated
  //  there if changed here (strings there are const only so no interpolation)
  val LENGTH_MIN = 1
  val LENGTH_MAX = 80

}

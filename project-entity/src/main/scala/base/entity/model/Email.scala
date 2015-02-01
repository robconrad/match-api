/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.entity.model

import base.entity.api.ApiStrings
import base.entity.error.model.ApiError
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

/**
 * Email field usable from DB to API layer, provides validation, etc.
 * @author rconrad
 */
case class Email(v: String) extends StringField {

  val minLength = Email.LENGTH_MIN
  val maxLength = Email.LENGTH_MAX
  lazy val lengthError: ApiError = ApiStrings.Field.emailLengthErrorDesc

  val companion = Email

}

object Email extends FieldCompanion[String, Email] {

  implicit def forwardsType(v: String) = Email(v)
  implicit def backwardsType(v: Email) = v.v

  val serializer = new CustomSerializer[Email](format => ({
    case JString(s) => Email(s)
  }, {
    case x: Email => JString(x.toString)
  }))

  // NOTE: these values are used in ApiStrings.Site and must be updated
  //  there if changed here (strings there are const only so no interpolation)
  val LENGTH_MIN = 5
  val LENGTH_MAX = 255

}

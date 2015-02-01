/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.entity.model

import java.net.{ MalformedURLException, URL }
import base.entity.api.ApiStrings
import base.entity.error.model.ApiError
import base.entity.error.ApiErrorService
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString
import spray.http.StatusCodes

/**
 * URL field usable from DB to API layer, provides validation, etc.
 * @author rconrad
 */
case class Url(v: String) extends StringField {

  val minLength = Url.LENGTH_MIN
  val maxLength = Url.LENGTH_MAX
  lazy val lengthError: ApiError = ApiStrings.Field.urlLengthErrorDesc

  val companion = Url

  override def validate = super.validate orElse {
    try {
      new URL(v)
      None
    } catch {
      case e: MalformedURLException =>
        ApiErrorService().statusCode(ApiStrings.Field.urlMalformedErrorDesc, StatusCodes.BadRequest)
    }
  }

}

object Url extends FieldCompanion[String, Url] {

  implicit def forwardsType(v: String) = Url(v)
  implicit def backwardsType(v: Url) = v.v

  val serializer = new CustomSerializer[Url](format => ({
    case JString(s) => Url(s)
  }, {
    case x: Url => JString(x.toString)
  }))

  // NOTE: these values are used in ApiStrings.Site and must be updated
  //  there if changed here (strings there are const only so no interpolation)
  val LENGTH_MIN = 5
  val LENGTH_MAX = 1000

}

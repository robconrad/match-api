/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 7:48 PM
 */

package base.entity.json

import base.common.lib.{ Genders, Currencies, Languages }
import base.entity.api.{ ApiErrorCodes, ApiVersions }
import base.entity.event.EventTypes
import base.entity.model.{ Email, Identifier, Name, Url }
import base.entity.question.QuestionSides
import org.json4s.DefaultFormats
import org.json4s.ext.EnumNameSerializer
import spray.http.StatusCodes

/**
 * Json4s formats
 * @author rconrad
 */
object JsonFormats {

  val default = DefaultFormats + StatusCodeSerializer + UuidSerializer + UrlSerializer

  val withHttpData = default + HttpDataSerializer

  val withEnumsAndFields = default +
    new EnumNameSerializer(ApiVersions) +
    new EnumNameSerializer(ApiErrorCodes) +
    new EnumNameSerializer(EventTypes) +
    new EnumNameSerializer(Genders) +
    new EnumNameSerializer(Currencies) +
    new EnumNameSerializer(Languages) +
    new EnumNameSerializer(QuestionSides) +
    Email.serializer +
    Identifier.serializer +
    Name.serializer +
    Url.serializer

}

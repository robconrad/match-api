/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 10:45 AM
 */

package base.entity.json

import base.common.lib.{ Genders, Currencies, Languages }
import base.entity.ApiErrorCodes
import base.entity.api.ApiVersions
import base.entity.model.{ Email, Identifier, Name, Url }
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
    new EnumNameSerializer(Genders) +
    new EnumNameSerializer(Currencies) +
    new EnumNameSerializer(Languages) +
    Email.serializer +
    Identifier.serializer +
    Name.serializer +
    Url.serializer

}

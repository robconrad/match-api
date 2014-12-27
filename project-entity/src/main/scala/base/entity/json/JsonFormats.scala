/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:10 PM
 */

package base.entity.json

import base.common.lib.{ Currencies, Languages }
import base.entity.model.{ Email, Identifier, Name, Url }
import org.json4s.DefaultFormats
import org.json4s.ext.EnumNameSerializer

/**
 * Json4s formats
 * @author rconrad
 */
object JsonFormats {

  val default = DefaultFormats + UuidSerializer + UrlSerializer

  val withHttpData = default + HttpDataSerializer

  val withEnumsAndFields = default +
    new EnumNameSerializer(Currencies) +
    new EnumNameSerializer(Languages) +
    Email.serializer +
    Identifier.serializer +
    Name.serializer +
    Url.serializer

}

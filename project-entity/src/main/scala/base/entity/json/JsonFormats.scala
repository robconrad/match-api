/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:24 PM
 */

package base.entity.json

import base.common.lib.{ Currencies, Genders, Languages }
import base.entity.api.{ ApiErrorCodes, ApiVersions }
import base.entity.command.CommandNames
import base.entity.error.model.ApiError
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.model.{ GroupModel, InviteModel }
import base.entity.model._
import base.entity.question.QuestionSides
import base.entity.user.model.LoginResponseModel
import org.json4s.DefaultFormats
import org.json4s.ext.EnumNameSerializer

/**
 * Json4s formats
 * @author rconrad
 */
object JsonFormats {

  val default = DefaultFormats +
    DateTimeSerializer +
    StatusCodeSerializer +
    UuidSerializer +
    UrlSerializer

  val withHttpData = default + HttpDataSerializer

  val withEnumsAndFields = default +
    new EnumNameSerializer(ApiVersions) +
    new EnumNameSerializer(ApiErrorCodes) +
    new EnumNameSerializer(CommandNames) +
    new EnumNameSerializer(EventTypes) +
    new EnumNameSerializer(Genders) +
    new EnumNameSerializer(Currencies) +
    new EnumNameSerializer(Languages) +
    new EnumNameSerializer(QuestionSides) +
    Email.serializer +
    Identifier.serializer +
    Name.serializer +
    Url.serializer

  val withModels = withEnumsAndFields +
    InviteModel.serializer +
    EventModel.serializer +
    GroupModel.serializer +
    LoginResponseModel.serializer +
    ApiError.serializer

}

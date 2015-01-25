/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:23 AM
 */

package base.entity.json

import java.net.URL
import java.util.UUID

import base.common.lib.{ Languages, Currencies, Genders }
import base.common.logging.Loggable
import base.common.random.RandomService
import base.entity.api.{ ApiErrorCodes, ApiVersions }
import base.entity.api.ApiVersions.ApiVersion
import base.entity.json.JsonFormats._
import base.entity.model.{ Url, Identifier, Name, Email }
import base.entity.test.EntityBaseSuite
import org.json4s.{ Formats, Extraction }
import org.json4s.JsonAST._
import org.json4s.native.{ JsonMethods, Serialization }
import spray.http.{ HttpData, StatusCode, StatusCodes }

/**
 * {{ Describe the high level purpose of JsonFormatsTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
sealed trait JsonFormatsTestValue[T] {
  def value: T
}
case class SerializerTestValue[T](value: T) extends JsonFormatsTestValue[T]
case class StatusCodeSerializerTestValue(value: StatusCode) extends JsonFormatsTestValue[StatusCode]

class JsonFormatsTest extends EntityBaseSuite with Loggable {

  private def testSerializer[T <: JsonFormatsTestValue[_]](formats: Formats,
                                                           value: T,
                                                           encoded: String,
                                                           jValue: JValue)(implicit m: Manifest[T]) {
    implicit val f = formats

    val string = Serialization.write(value)
    assert(string == "{\"value\":" + encoded + "}")

    val parsed = JsonMethods.parse(string)
    assert(parsed == JObject(List(("value", jValue))))

    val extracted = Extraction.extract[T](parsed)
    assert(extracted == value)
  }

  private def testStringSerializer[T <: AnyRef](formats: Formats,
                                                value: T)(implicit m: Manifest[T]) {
    testSerializer(formats, SerializerTestValue(value), "\"" + value + "\"", JString(value.toString))
  }

  test("StatusCodeSerializer") {
    val value = StatusCodes.BadRequest
    testSerializer(default, StatusCodeSerializerTestValue(value), value.intValue.toString, JInt(value.intValue))
  }

  test("UuidSerializer") {
    testStringSerializer(default, RandomService().uuid)
  }

  test("URLSerializer") {
    testStringSerializer(default, new URL("http://google.com"))
  }

  test("HttpDataSerializer") {
    val value = HttpData("some http data")
    testSerializer(withHttpData, SerializerTestValue(value), "\"" + value.asString + "\"", JString(value.asString))
  }

  test("ApiVersionsSerializer") {
    testStringSerializer(withModels, ApiVersions.V01)
  }

  test("ApiErrorCodesSerializer") {
    testStringSerializer(withModels, ApiErrorCodes.TEST)
  }

  test("GendersSerializer") {
    testStringSerializer(withModels, Genders.male)
  }

  test("CurrenciesSerializer") {
    testStringSerializer(withModels, Currencies.USD)
  }

  test("LanguagesSerializer") {
    testStringSerializer(withModels, Languages.eng)
  }

  test("EmailSerializer") {
    testStringSerializer(withModels, Email("email"))
  }

  test("IdentifierSerializer") {
    testStringSerializer(withModels, Identifier("identifier"))
  }

  test("NameSerializer") {
    testStringSerializer(withModels, Name("name"))
  }

  test("UrlSerializer") {
    testStringSerializer(withModels, Url("url"))
  }

}

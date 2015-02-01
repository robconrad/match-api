/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 11:18 AM
 */

package base.entity.error

import base.common.lib.Encoding
import base.common.logging.Loggable
import base.entity.api.ApiErrorCodes
import base.entity.test.EntityBaseSuite
import com.google.common.hash.Hashing
import spray.http.StatusCodes

/**
 * {{ Describe the high level purpose of ApiErrorTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ApiErrorTest extends EntityBaseSuite {

  private val msg = "error message"
  private val status = StatusCodes.Forbidden
  private val defaultStatus = StatusCodes.BadRequest
  private val code = ApiErrorCodes.TEST
  private val uniqueIdSeed = "unique seed"
  private val uniqueIdThrowable = new RuntimeException("test")
  private val param = "param"

  private def uniqueId(seed: String) = Hashing.md5.hashString(seed, Encoding.CHARSET_UTF8).toString

  test("apply(message: String)") {
    val e = ApiErrorService().badRequest(msg)
    assert(e.command == None)
    assert(e.status == defaultStatus)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(s"$defaultStatus, $msg"))
  }

  test("apply(message: String, status: StatusCode, code: ErrorCode, uniqueIdSeed: String)") {
    val e = ApiErrorService().errorCodeSeed(msg, status, code, uniqueIdSeed)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == Option(code))
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(uniqueIdSeed))
  }

  test("apply(message: String, status: StatusCode)") {
    val e = ApiErrorService().statusCode(msg, status)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(s"$status, $msg"))
  }

  test("apply(message: String, status: StatusCode, code: ErrorCode)") {
    val e = ApiErrorService().errorCode(msg, status, code)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == Option(code))
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(s"$status, $code"))
  }

  test("apply(message: String, status: StatusCode, uniqueIdSeed: Throwable)") {
    val e = ApiErrorService().throwable(msg, status, uniqueIdThrowable)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(s"$status, ${Loggable.stackTraceToString(uniqueIdThrowable)}"))
  }

  test("apply(message: String, status: StatusCode, uniqueIdSeed: String)") {
    val e = ApiErrorService().statusCodeSeed(msg, status, uniqueIdSeed)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(uniqueIdSeed))
  }

  test("apply(msg: String, status: StatusCode, code: Option[ErrorCode], param: Option[String], uniqueIdSeed: String)") {
    val e = ApiErrorService().full(msg, status, Option(code), Option(param), uniqueIdSeed)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == Option(code))
    assert(e.param == Option(param))
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(uniqueIdSeed))
  }

}

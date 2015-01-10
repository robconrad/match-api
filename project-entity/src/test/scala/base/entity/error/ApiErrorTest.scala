/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 2:42 PM
 */

package base.entity.error

import base.common.lib.Encoding
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
  private val cmd = "command"
  private val status = StatusCodes.Forbidden
  private val defaultStatus = StatusCodes.BadRequest
  private val code = ApiErrorCodes.TEST
  private val uniqueIdSeed = "unique seed"
  private val uniqueIdThrowable = new RuntimeException("test")
  private val param = "param"

  private def uniqueId(seed: String) = Hashing.md5.hashString(seed, Encoding.CHARSET_UTF8).toString

  test("apply(message: String)") {
    val e = ApiError(msg)
    assert(e.command == None)
    assert(e.status == defaultStatus)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(s"$defaultStatus, $msg"))
  }

  test("apply(message: String, code: ErrorCode, uniqueIdSeed: String)") {
    val e = ApiError(msg, code, uniqueIdSeed)
    assert(e.command == None)
    assert(e.status == defaultStatus)
    assert(e.code == Option(code))
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(uniqueIdSeed))
  }

  test("apply(message: String, status: StatusCode)") {
    val e = ApiError(msg, status)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(s"$status, $msg"))
  }

  test("apply(message: String, status: StatusCode, uniqueIdSeed: Throwable)") {
    val e = ApiError(msg, status, uniqueIdThrowable)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(s"$status, $uniqueIdThrowable"))
  }

  test("apply(message: String, status: StatusCode, uniqueIdSeed: String)") {
    val e = ApiError(msg, status, uniqueIdSeed)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == None)
    assert(e.param == None)
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(uniqueIdSeed))
  }

  test("apply(msg: String, status: StatusCode, code: Option[ErrorCode], param: Option[String], uniqueIdSeed: String)") {
    val e = ApiError(msg, status, Option(code), Option(param), uniqueIdSeed)
    assert(e.command == None)
    assert(e.status == status)
    assert(e.code == Option(code))
    assert(e.param == Option(param))
    assert(e.message == msg)
    assert(e.uniqueId == uniqueId(uniqueIdSeed))
  }

}

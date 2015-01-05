/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 6:11 PM
 */

package base.common.service

import java.security.InvalidParameterException

import base.common.test.BaseSuite

import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of BaseConfigTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ServicesBootstrapTest extends BaseSuite {

  private val bootstrap = new ServicesBootstrap {
    val registered = true

    val TEST = Keys("test")
    val DATA = Keys(TEST, "data")

    def k(s: String): Key = Keys(DATA, s)

    lazy val brokenDuration = {
      intercept[InvalidParameterException] {
        val d: Duration = k("brokenDuration")
      }
      true
    }

    lazy val day: Duration = k("day")
    lazy val days: Duration = k("days")
    lazy val hour: Duration = k("hour")
    lazy val hours: Duration = k("hours")
    lazy val minute: Duration = k("minute")
    lazy val minutes: Duration = k("minutes")
    lazy val second: Duration = k("second")
    lazy val seconds: Duration = k("seconds")
    lazy val milli: Duration = k("milli")
    lazy val millis: Duration = k("millis")
    lazy val millisecond: Duration = k("millisecond")
    lazy val milliseconds: Duration = k("milliseconds")

    lazy val string: String = k("string")
    lazy val stringOpt: Option[String] = k("string")

    lazy val stringList: List[String] = k("stringList")
    lazy val stringListOpt: Option[List[String]] = k("stringList")

    lazy val int: Int = k("int")
    lazy val intOpt: Option[Int] = k("int")

    lazy val intList: List[Int] = k("intList")
    lazy val intListOpt: Option[List[Int]] = k("intList")

    lazy val long: Long = k("long")
    lazy val longOpt: Option[Long] = k("long")

    lazy val longList: List[Long] = k("longList")
    lazy val longListOpt: Option[List[Long]] = k("longList")

    lazy val float: Float = k("float")
    lazy val floatOpt: Option[Float] = k("float")

    lazy val floatList: List[Float] = k("floatList")
    lazy val floatListOpt: Option[List[Float]] = k("floatList")

    lazy val double: Double = k("double")
    lazy val doubleOpt: Option[Double] = k("double")

    lazy val doubleList: List[Double] = k("doubleList")
    lazy val doubleListOpt: Option[List[Double]] = k("doubleList")

    lazy val bool: Boolean = k("bool")
    lazy val boolOpt: Option[Boolean] = k("bool")

    lazy val boolList: List[Boolean] = k("boolList")
    lazy val boolListOpt: Option[List[Boolean]] = k("boolList")
  }

  import bootstrap._

  test("periods") {
    assert(brokenDuration)
    assert(day == 1.day)
    assert(days == 2.days)
    assert(hour == 1.hour)
    assert(hours == 2.hours)
    assert(minute == 1.minute)
    assert(minutes == 2.minutes)
    assert(second == 1.second)
    assert(seconds == 2.seconds)
    assert(milli == 1.milli)
    assert(millis == 2.millis)
    assert(millisecond == 1.millisecond)
    assert(milliseconds == 2.milliseconds)
  }

  test("data types") {
    assert(string == "string")
    assert(stringOpt.contains("string"))
    assert(stringList == List("string1", "string2"))
    assert(stringListOpt.contains(List("string1", "string2")))
    assert(int == 1)
    assert(intOpt.contains(1))
    assert(intList == List(1, 2))
    assert(intListOpt.contains(List(1, 2)))
    assert(long == 1L)
    assert(longOpt.contains(1L))
    assert(longList == List(1L, 2L))
    assert(longListOpt.contains(List(1L, 2L)))
    assert(float == 1.1f)
    assert(floatOpt.contains(1.1f))
    assert(floatList == List(1.1f, 2.2f))
    assert(floatListOpt.contains(List(1.1f, 2.2f)))
    assert(double == 1.1d)
    assert(doubleOpt.contains(1.1d))
    assert(doubleList == List(1.1d, 2.2d))
    assert(doubleListOpt.contains(List(1.1d, 2.2d)))
    assert(bool)
    assert(boolOpt.contains(true))
    assert(boolList == List(true, false))
    assert(boolListOpt.contains(List(true, false)))
  }

}

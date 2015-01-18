/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 10:42 PM
 */

package base.common.lib

import java.security.InvalidParameterException
import java.util.UUID

import com.typesafe.config.{ Config, ConfigFactory }
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

import scala.collection.JavaConversions._
import scala.concurrent.duration._

/**
 * Instance of a TypeSafe config with useful accessor functions
 * @author rconrad
 */
class BaseConfig(conf: Config) {

  private val periodFormats = List(
    new PeriodFormatterBuilder().appendDays().appendSuffix(" day").toFormatter,
    new PeriodFormatterBuilder().appendDays().appendSuffix(" days").toFormatter,
    new PeriodFormatterBuilder().appendHours().appendSuffix(" hour").toFormatter,
    new PeriodFormatterBuilder().appendHours().appendSuffix(" hours").toFormatter,
    new PeriodFormatterBuilder().appendMinutes().appendSuffix(" minute").toFormatter,
    new PeriodFormatterBuilder().appendMinutes().appendSuffix(" minutes").toFormatter,
    new PeriodFormatterBuilder().appendSeconds().appendSuffix(" second").toFormatter,
    new PeriodFormatterBuilder().appendSeconds().appendSuffix(" seconds").toFormatter,
    new PeriodFormatterBuilder().appendMillis().appendSuffix(" milli").toFormatter,
    new PeriodFormatterBuilder().appendMillis().appendSuffix(" millis").toFormatter,
    new PeriodFormatterBuilder().appendMillis().appendSuffix(" millisecond").toFormatter,
    new PeriodFormatterBuilder().appendMillis().appendSuffix(" milliseconds").toFormatter)

  // scalastyle:off line.size.limit
  def getString(name: String) = Tryo(conf.getString(name))
  def getStringList(name: String): Option[List[String]] = Tryo(conf.getStringList(name)).map(_.toList)
  def getUuid(name: String) = Tryo(UUID.fromString(conf.getString(name)))
  def getUuidList(name: String): Option[List[UUID]] = Tryo(conf.getStringList(name)).map(_.toList.map(UUID.fromString))
  def getInt(name: String) = Tryo(conf.getInt(name))
  def getIntList(name: String): Option[List[Int]] = Tryo(conf.getIntList(name)).map(_.toList.map(_.toInt))
  def getLong(name: String) = Tryo(conf.getLong(name))
  def getLongList(name: String): Option[List[Long]] = Tryo(conf.getLongList(name)).map(_.toList.map(_.toLong))
  def getFloat(name: String) = Tryo(conf.getDouble(name).toFloat)
  def getFloatList(name: String): Option[List[Float]] = Tryo(conf.getDoubleList(name).toList.map(_.toFloat))
  def getDouble(name: String) = Tryo(conf.getDouble(name))
  def getDoubleList(name: String): Option[List[Double]] = Tryo(conf.getDoubleList(name).toList.map(_.toDouble))
  def getBool(name: String) = Tryo(conf.getBoolean(name))
  def getBoolList(name: String): Option[List[Boolean]] = Tryo(conf.getBooleanList(name).toList.map(_.booleanValue()))

  def getObjectList(name: String) = Tryo(conf.getObjectList(name).toList.map(o => mapAsScalaMap(o.unwrapped())))
  def getConfigList(name: String): Option[List[Config]] = Tryo(conf.getConfigList(name).asInstanceOf[java.util.List[Config]]).map(_.toList)

  // scalastyle:on line.size.limit

  def getOptionPeriod(name: String) = getString(name).map { value =>
    var ret: Option[Period] = None
    for (i <- 0 until periodFormats.length) {
      try {
        ret = Option(periodFormats(i).parsePeriod(value))
      } catch {
        case e: IllegalArgumentException => //do nothing
      }
    }
    ret.getOrElse(throw new InvalidParameterException(s"could not find period format for $name"))
  }

  def getOptionFiniteDuration(name: String) = getOptionPeriod(name).map(_.toStandardDuration.getMillis.millis)

}

object BaseConfig {

  val tsConf = ConfigFactory.load().withFallback(ConfigFactory.defaultReference())
  val conf = new BaseConfig(tsConf)

  def apply() = conf

}

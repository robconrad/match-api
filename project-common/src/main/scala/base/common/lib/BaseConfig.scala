/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:30 PM
 */

package base.common.lib

import java.security.InvalidParameterException

import com.typesafe.config.{ Config, ConfigFactory }
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

import scala.collection.JavaConversions._
import scala.concurrent.duration._

/**
 * Instance of a TypeSafe config with useful accessor functions
 * @author rconrad
 */
private[common] class BaseConfig(conf: Config) {

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
  def getOptionString(name: String) = Tryo(conf.getString(name))
  def getString(name: String, fallback: String) = getOptionString(name) getOrElse fallback

  def getOptionStringList(name: String): Option[List[String]] = Tryo(conf.getStringList(name)).map(_.toList)
  def getStringList(name: String, fallback: List[String]): List[String] = getOptionStringList(name) getOrElse fallback

  def getOptionInt(name: String) = Tryo(conf.getInt(name))
  def getInt(name: String, fallback: Int) = getOptionInt(name) getOrElse fallback

  def getOptionIntList(name: String): Option[List[Int]] = Tryo(conf.getIntList(name)).map(_.toList.map(_.toInt))
  def getIntList(name: String, fallback: List[Int]): List[Int] = getOptionIntList(name) getOrElse fallback

  def getOptionLong(name: String) = Tryo(conf.getLong(name))
  def getLong(name: String, fallback: Long) = getOptionLong(name) getOrElse fallback

  def getOptionLongList(name: String): Option[List[Long]] = Tryo(conf.getLongList(name)).map(_.toList.map(_.toLong))
  def getLongList(name: String, fallback: List[Long]): List[Long] = getOptionLongList(name) getOrElse fallback

  def getOptionFloat(name: String) = Tryo(conf.getDouble(name).toFloat)
  def getFloat(name: String, fallback: Double) = getOptionDouble(name) getOrElse fallback

  def getOptionDouble(name: String) = Tryo(conf.getDouble(name))
  def getDouble(name: String, fallback: Double) = getOptionDouble(name) getOrElse fallback

  def getOptionBool(name: String) = Tryo(conf.getBoolean(name))
  def getBool(name: String, fallback: Boolean) = getOptionBool(name) getOrElse fallback

  def getMap(name: String, fallback: Map[String, Any]) = Tryo(mapAsScalaMap(conf.getObject(name).unwrapped()), fallback)

  def getOptionObjectList(name: String) = Tryo(conf.getObjectList(name).toList.map(o => mapAsScalaMap(o.unwrapped())))

  def getOptionConfigList(name: String): Option[List[Config]] = Tryo(conf.getConfigList(name).asInstanceOf[java.util.List[Config]]).map(_.toList)

  def getDoubleList(name: String) = Tryo(conf.getDoubleList(name))
  // scalastyle:on line.size.limit

  def getOptionPeriod(name: String) = getOptionString(name).map { value =>
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

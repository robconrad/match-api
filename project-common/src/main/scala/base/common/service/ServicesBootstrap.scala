/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:30 PM
 */

package base.common.service

import base.common.lib.{ BaseConfig, Currencies, Dispatchable }
import base.common.logging.Loggable
import com.typesafe.config.ConfigException.Missing

/**
 * Injects configuration into Services and boots them up. If it's configurable, it belongs here.
 * @author rconrad
 */
trait ServicesBootstrap extends Loggable with Dispatchable {

  /**
   * Convenience for managing the location of a Key
   */
  protected case class Key(s: String) {
    override def toString = s
  }

  protected object Keys {
    def apply(s: String*): Key = Key(s.mkString("."))
    def apply(k: Key, s: String*): Key = Key(s"$k." + s.mkString("."))
  }

  /**
   * Process all config value pulls through a central function that writes
   *  the requested key and returned value to log for posterity
   */
  protected def get[T](key: Key, fun: String => Option[T]): T = {
    val res = fun(key.s).getOrElse(throw new Missing(key.toString))
    info(s"Conf: $key = $res")
    res
  }

  /**
   * The magic that makes the service configs below so clean. Essentially the
   *  ctors know what param types they way, and the implicits just make it happen
   */
  // scalastyle:off line.size.limit
  implicit protected val config = BaseConfig()
  implicit protected def getString(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionString)
  implicit protected def getOptionString(k: Key)(implicit config: BaseConfig) = config.getOptionString(k.s)
  implicit protected def getStringList(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionStringList)
  implicit protected def getCurrencySet(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionStringList).map(Currencies.withName).toSet
  implicit protected def getOptionStringList(k: Key)(implicit config: BaseConfig) = config.getOptionStringList(k.s)
  implicit protected def getBool(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionBool)
  implicit protected def getOptionInt(k: Key)(implicit config: BaseConfig) = config.getOptionInt(k.s)
  implicit protected def getInt(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionInt)
  implicit protected def getIntList(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionIntList)
  implicit protected def getOptionLong(k: Key)(implicit config: BaseConfig) = config.getOptionLong(k.s)
  implicit protected def getLong(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionLong)
  implicit protected def getLongList(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionLongList)
  implicit protected def getFloat(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionFloat)
  implicit protected def getDouble(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionDouble)
  implicit protected def getPeriod(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionPeriod)
  implicit protected def getFiniteDuration(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionFiniteDuration)
  implicit protected def getObjectList(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionObjectList)
  implicit protected def getConfigList(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionConfigList)
  // scalastyle:on

  /**
   * Config Sections
   */
  protected val AKKA = "akka"
  protected val BASE = "base"

  /**
   * Trigger and status indicator for executing bootstrap startup behavior (i.e. registering services)
   */
  val registered: Boolean

}

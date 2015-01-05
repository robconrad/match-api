/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 6:07 PM
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
  implicit protected def getString(k: Key)(implicit config: BaseConfig) = get(k, config.getString)
  implicit protected def getOptionString(k: Key)(implicit config: BaseConfig) = config.getString(k.s)
  implicit protected def getStringList(k: Key)(implicit config: BaseConfig) = get(k, config.getStringList)
  implicit protected def getOptionStringList(k: Key)(implicit config: BaseConfig) = config.getStringList(k.s)
  implicit protected def getCurrencySet(k: Key)(implicit config: BaseConfig) = get(k, config.getStringList).map(Currencies.withName).toSet
  implicit protected def getBool(k: Key)(implicit config: BaseConfig) = get(k, config.getBool)
  implicit protected def getOptionBool(k: Key)(implicit config: BaseConfig) = config.getBool(k.s)
  implicit protected def getBoolList(k: Key)(implicit config: BaseConfig) = get(k, config.getBoolList)
  implicit protected def getOptionBoolList(k: Key)(implicit config: BaseConfig) = config.getBoolList(k.s)
  implicit protected def getInt(k: Key)(implicit config: BaseConfig) = get(k, config.getInt)
  implicit protected def getOptionInt(k: Key)(implicit config: BaseConfig) = config.getInt(k.s)
  implicit protected def getIntList(k: Key)(implicit config: BaseConfig) = get(k, config.getIntList)
  implicit protected def getOptionIntList(k: Key)(implicit config: BaseConfig) = config.getIntList(k.s)
  implicit protected def getLong(k: Key)(implicit config: BaseConfig) = get(k, config.getLong)
  implicit protected def getOptionLong(k: Key)(implicit config: BaseConfig) = config.getLong(k.s)
  implicit protected def getLongList(k: Key)(implicit config: BaseConfig) = get(k, config.getLongList)
  implicit protected def getOptionLongList(k: Key)(implicit config: BaseConfig) = config.getLongList(k.s)
  implicit protected def getFloat(k: Key)(implicit config: BaseConfig) = get(k, config.getFloat)
  implicit protected def getOptionFloat(k: Key)(implicit config: BaseConfig) = config.getFloat(k.s)
  implicit protected def getFloatList(k: Key)(implicit config: BaseConfig) = get(k, config.getFloatList)
  implicit protected def getOptionFloatList(k: Key)(implicit config: BaseConfig) = config.getFloatList(k.s)
  implicit protected def getDouble(k: Key)(implicit config: BaseConfig) = get(k, config.getDouble)
  implicit protected def getOptionDouble(k: Key)(implicit config: BaseConfig) = config.getDouble(k.s)
  implicit protected def getDoubleList(k: Key)(implicit config: BaseConfig) = get(k, config.getDoubleList)
  implicit protected def getOptionDoubleList(k: Key)(implicit config: BaseConfig) = config.getDoubleList(k.s)
  implicit protected def getPeriod(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionPeriod)
  implicit protected def getFiniteDuration(k: Key)(implicit config: BaseConfig) = get(k, config.getOptionFiniteDuration)
  implicit protected def getObjectList(k: Key)(implicit config: BaseConfig) = get(k, config.getObjectList)
  implicit protected def getConfigList(k: Key)(implicit config: BaseConfig) = get(k, config.getConfigList)
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

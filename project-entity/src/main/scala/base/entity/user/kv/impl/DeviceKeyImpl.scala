/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 6:39 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.time.TimeService
import base.entity.kv.serializer.SerializerImplicits._
import base.entity.user.kv.DeviceKey
import base.entity.user.kv.impl.DeviceKeyImpl._
import scredis.keys.{ HashKey, HashKeyProp, HashKeyProps }
import scredis.serialization.{UTF8StringWriter, UTF8StringReader}

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class DeviceKeyImpl(keyFactory: HashKeyProps => HashKey[Short, UUID])
    extends DeviceKey
    with Dispatchable {

  private lazy val key = keyFactory(props)

  def getToken = key.get[UUID](TokenProp)
  def setToken(token: UUID) = key.set(TokenProp, token)

  def getUserId = key.get[UUID](UserIdProp)
  def setUserId(userId: UUID) = key.set(UserIdProp, userId)

  def setTokenAndUserId(token: UUID, userId: UUID) = {
    val props = Map[HashKeyProp, Array[Byte]](
      UpdatedProp -> dateTimeSerializer.write(TimeService().now),
      TokenProp -> uuidWriter.write(token),
      UserIdProp -> uuidWriter.write(userId))
    key.mSet(props)
  }

  // scalastyle:off null
  def set(appVersion: String,
          locale: String,
          model: Option[String],
          cordova: Option[String],
          platform: Option[String],
          version: Option[String]) = {
    val props = Map[HashKeyProp, Array[Byte]](
      AppVersionProp -> UTF8StringWriter.write(appVersion),
      LocaleProp -> UTF8StringWriter.write(locale),
      ModelProp -> model.map(UTF8StringWriter.write).orNull,
      CordovaProp -> cordova.map(UTF8StringWriter.write).orNull,
      PlatformProp -> platform.map(UTF8StringWriter.write).orNull,
      VersionProp -> version.map(UTF8StringWriter.write).orNull)
    key.mSet(props.filter(_._2 != null))
  }

  def getAppVersion = key.get[String](AppVersionProp)
  def getLocale = key.get[String](LocaleProp)
  def getModel = key.get[String](ModelProp)
  def getCordova = key.get[String](CordovaProp)
  def getPlatform = key.get[String](PlatformProp)
  def getVersion = key.get[String](VersionProp)

}

private[impl] object DeviceKeyImpl {

  val CreatedProp = HashKeyProp("created")
  val UpdatedProp = HashKeyProp("updated")
  val TokenProp = HashKeyProp("token")
  val UserIdProp = HashKeyProp("userId")
  val AppVersionProp = HashKeyProp("appVersion")
  val LocaleProp = HashKeyProp("locale")
  val ModelProp = HashKeyProp("model")
  val CordovaProp = HashKeyProp("cordova")
  val PlatformProp = HashKeyProp("platform")
  val VersionProp = HashKeyProp("version")

  val props = HashKeyProps(Set(
    CreatedProp,
    UpdatedProp,
    TokenProp,
    UserIdProp,
    AppVersionProp,
    LocaleProp,
    ModelProp,
    CordovaProp,
    PlatformProp,
    VersionProp))

}

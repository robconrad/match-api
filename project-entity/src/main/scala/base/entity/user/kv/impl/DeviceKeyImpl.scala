/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:30 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.KeyProps.UpdatedProp
import base.entity.kv.impl.HashKeyImpl
import base.entity.user.kv.DeviceKey
import base.entity.user.kv.UserKeyProps._

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class DeviceKeyImpl(val keyValue: UUID)
    extends HashKeyImpl[UUID]
    with DeviceKey {

  def getToken = get(TokenProp).map(_.map(read[UUID]))
  def setToken(token: UUID) = set(TokenProp, write(token))

  def getUserId = get(UserIdProp).map(_.map(read[UUID]))
  def setUserId(userId: UUID) = set(UserIdProp, write(userId))

  def setTokenAndUserId(token: UUID, userId: UUID) = {
    val props = Map[Prop, Array[Byte]](
      UpdatedProp -> write(TimeService()),
      TokenProp -> write(token),
      UserIdProp -> write(userId))
    mSet(props)
  }

  def set(appVersion: String,
          locale: String,
          model: Option[String],
          cordova: Option[String],
          platform: Option[String],
          version: Option[String]) = {
    val props = Map[Prop, Array[Byte]](
      AppVersionProp -> write(appVersion),
      LocaleProp -> write(locale),
      ModelProp -> model.map(write[String]).orNull,
      CordovaProp -> cordova.map(write[String]).orNull,
      PlatformProp -> platform.map(write[String]).orNull,
      VersionProp -> version.map(write[String]).orNull)
    mSet(props.filter(_._2 != null))
  }

  def getAppVersion = get(AppVersionProp).map(_.map(read[String]))
  def getLocale = get(LocaleProp).map(_.map(read[String]))
  def getModel = get(ModelProp).map(_.map(read[String]))
  def getCordova = get(CordovaProp).map(_.map(read[String]))
  def getPlatform = get(PlatformProp).map(_.map(read[String]))
  def getVersion = get(VersionProp).map(_.map(read[String]))

}

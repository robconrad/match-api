/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 10:02 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.KeyLogger
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
class DeviceKeyImpl(val token: String,
                    protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends HashKeyImpl with DeviceKey {

  def getToken = getId(TokenProp)
  def setToken(token: UUID) = set(TokenProp, token)

  def getUserId = getId(UserIdProp)
  def setUserId(userId: UUID) = set(UserIdProp, userId)

  def setTokenAndUserId(token: UUID, userId: UUID) = {
    val props = Map[Prop, Any](
      UpdatedProp -> TimeService().asString(),
      TokenProp -> token,
      UserIdProp -> userId)
    set(props)
  }

  def set(appVersion: String,
          locale: String,
          model: String,
          cordova: String,
          platform: String,
          version: String) = {
    val props = Map[Prop, Any](
      AppVersionProp -> appVersion,
      LocaleProp -> locale,
      ModelProp -> model,
      CordovaProp -> cordova,
      PlatformProp -> platform,
      VersionProp -> version)
    set(props)
  }

  def getAppVersion = getString(AppVersionProp)
  def getLocale = getString(LocaleProp)
  def getModel = getString(ModelProp)
  def getCordova = getString(CordovaProp)
  def getPlatform = getString(PlatformProp)
  def getVersion = getString(VersionProp)

}

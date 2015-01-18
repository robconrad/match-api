/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:33 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.KeyProps.UpdatedProp
import base.entity.kv.PrivateHashKey
import base.entity.kv.impl.HashKeyImpl
import base.entity.user.kv.DeviceKey
import base.entity.user.kv.UserKeyProps._

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class DeviceKeyImpl(protected val key: PrivateHashKey)(implicit protected val p: Pipeline)
    extends DeviceKey with HashKeyImpl {

  def getToken = key.getId(TokenProp)

  def getUserId = key.getId(UserIdProp)

  def setTokenAndUserId(token: UUID, userId: UUID) = {
    val props = Map[Prop, Any](
      UpdatedProp -> TimeService().asString(),
      TokenProp -> token,
      UserIdProp -> userId)
    key.set(props)
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
    key.set(props)
  }

}

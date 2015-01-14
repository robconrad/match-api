/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:10 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.{ PrivateHashKey, KeyLogger }
import base.entity.kv.KeyProps.{ UpdatedProp, CreatedProp }
import base.entity.kv.impl.{ HashKeyImpl, PrivateHashKeyImpl }
import base.entity.user.DeviceKey
import base.entity.user.UserKeyProps._

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class DeviceKeyImpl(protected val key: PrivateHashKey) extends DeviceKey with HashKeyImpl {

  def getToken(implicit p: Pipeline) = key.getId(TokenProp)

  def getUserId(implicit p: Pipeline) = key.getId(UserIdProp)

  def setTokenAndUserId(token: UUID, userId: UUID)(implicit p: Pipeline) = {
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
          version: String)(implicit p: Pipeline) = {
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

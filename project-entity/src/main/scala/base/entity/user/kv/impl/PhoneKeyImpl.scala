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
import base.entity.user.kv.{ PhoneKey, UserKeyProps }
import base.entity.user.kv.UserKeyProps.{ CodeProp, UserIdProp }

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneKeyImpl(protected val key: PrivateHashKey)(implicit protected val p: Pipeline)
    extends PhoneKey with HashKeyImpl {

  def getCode = key.getString(CodeProp)
  def setCode(code: String) = {
    val props = Map[Prop, Any](
      UpdatedProp -> TimeService().asString(),
      CodeProp -> code)
    key.set(props)
  }

  def getUserId = key.getId(UserIdProp)
  def setUserId(userId: UUID) = {
    val props = Map[Prop, Any](
      UpdatedProp -> TimeService().asString(),
      UserIdProp -> userId)
    key.set(props)
  }

}

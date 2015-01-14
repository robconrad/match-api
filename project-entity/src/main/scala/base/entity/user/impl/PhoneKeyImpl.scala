/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:10 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.{ PrivateHashKey, KeyLogger }
import base.entity.kv.KeyProps.UpdatedProp
import base.entity.kv.impl.{ HashKeyImpl, PrivateHashKeyImpl }
import base.entity.user.PhoneKey
import base.entity.user.UserKeyProps.{ CodeProp, UserIdProp }

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneKeyImpl(protected val key: PrivateHashKey) extends PhoneKey with HashKeyImpl {

  def getCode(implicit p: Pipeline) = key.getString(CodeProp)
  def setCode(code: String)(implicit p: Pipeline) = {
    val props = Map[Prop, Any](
      UpdatedProp -> TimeService().asString(),
      CodeProp -> code)
    key.set(props)
  }

  def getUserId(implicit p: Pipeline) = key.getId(UserIdProp)
  def setUserId(userId: UUID)(implicit p: Pipeline) = key.set(UserIdProp, userId)

}

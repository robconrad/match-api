/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:10 PM
 */

package base.entity.user.impl

import base.common.lib.Genders._
import base.common.lib.{ Genders, Tryo }
import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.{ PrivateHashKey, KeyLogger }
import base.entity.kv.KeyProps.UpdatedProp
import base.entity.kv.impl.{ HashKeyImpl, PrivateHashKeyImpl }
import base.entity.user.UserKey
import base.entity.user.UserKeyProps.{ LastLoginProp, GenderProp, NameProp }
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserKeyImpl(protected val key: PrivateHashKey) extends UserKey with HashKeyImpl {

  private val userKeyGetProps = Array[Prop](NameProp, GenderProp)
  def getNameAndGender(implicit p: Pipeline) = {
    key.get(userKeyGetProps).map { props =>
      val name = props.get(NameProp).flatten
      val gender = Tryo(props.get(GenderProp).flatten.map(Genders.withName)).flatten
      (name, gender)
    }
  }

  def setNameAndGender(name: String, gender: Gender)(implicit p: Pipeline) = {
    val props = Map[Prop, Any](
      NameProp -> name,
      GenderProp -> gender,
      UpdatedProp -> TimeService().asString())
    key.set(props)
  }

  def getLastLogin(implicit p: Pipeline) = key.getDateTime(LastLoginProp)
  def setLastLogin(time: DateTime)(implicit p: Pipeline) = key.set(LastLoginProp, TimeService().asString(time))

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/27/15 7:48 PM
 */

package base.entity.user.kv.impl

import base.common.lib.{ Genders, Tryo }
import base.common.time.TimeService
import base.entity.facebook.FacebookInfo
import base.entity.kv.Key._
import base.entity.kv.{ Key, KeyLogger }
import base.entity.kv.KeyProps.UpdatedProp
import base.entity.kv.impl.HashKeyImpl
import base.entity.user.kv.UserKey
import base.entity.user.kv.UserKeyProps._
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserKeyImpl(val token: Array[Byte],
                  protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends HashKeyImpl with UserKey {

  private val userKeyGetProps = Array[Prop](NameProp, GenderProp)
  def getNameAndGender = {
    get(userKeyGetProps).map { props =>
      val name = props.get(NameProp).flatten
      val gender = Tryo(props.get(GenderProp).flatten.map(Genders.withName)).flatten
      (name, gender)
    }
  }

  val phoneVerifiedProps = Array[Prop](PhoneProp, PhoneVerifiedProp)
  def getPhoneVerified: Future[(Option[String], Option[Boolean])] = {
    get(phoneVerifiedProps).map { props =>
      (props.get(PhoneProp).flatten, props.get(PhoneVerifiedProp).flatten.map(Key.string2Boolean))
    }
  }

  def setPhoneVerified(phone: String, verified: Boolean) = {
    val setPhone = set(PhoneProp, phone)
    val setVerified = setFlag(PhoneVerifiedProp, verified)
    Future.sequence(Set(setPhone, setVerified)).map(_.reduce(_ && _))
  }

  def setFacebookInfo(fbInfo: FacebookInfo) = {
    val props = Map[Prop, Any](
      NameProp -> fbInfo.firstName,
      GenderProp -> fbInfo.gender,
      FacebookIdProp -> fbInfo.id,
      LocaleProp -> fbInfo.locale,
      UpdatedProp -> TimeService().asString())
    set(props)
  }

  def getFacebookId = getString(FacebookIdProp)

  def getLocale = getString(LocaleProp)

  def getLastLogin = getDateTime(LastLoginProp)
  def setLastLogin(time: DateTime) = set(LastLoginProp, TimeService().asString(time))

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 3:00 PM
 */

package base.entity.user.kv.impl

import base.common.time.TimeService
import base.entity.facebook.FacebookInfo
import base.entity.kv.Key._
import base.entity.kv.KeyProps.UpdatedProp
import base.entity.kv.impl.HashKeyImpl
import base.entity.kv.{ Key, KeyLogger }
import base.entity.user.kv.UserKeyProps._
import base.entity.user.kv.{ UserLoginAttributes, UserKey, UserPhoneAttributes }
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

  def getName = getString(NameProp)

  val phoneVerifiedProps = Array[Prop](PhoneProp, PhoneCodeProp, PhoneVerifiedProp)
  def getPhoneAttributes = {
    get(phoneVerifiedProps).map { props =>
      (props.get(PhoneProp).flatten,
        props.get(PhoneCodeProp).flatten,
        props.get(PhoneVerifiedProp).flatten.map(Key.string2Boolean)) match {
          case (Some(phone), Some(code), Some(verified)) => Option(UserPhoneAttributes(phone, code, verified))
          case _                                         => None
        }
    }
  }

  def setPhoneAttributes(attributes: UserPhoneAttributes) = {
    val setCreated = create().map(result => true)
    val setPhone = set(PhoneProp, attributes.phone)
    val setVerified = setPhoneVerified(attributes.verified)
    val setCode = set(PhoneCodeProp, attributes.code)
    Future.sequence(Set(setCreated, setPhone, setVerified, setCode)).map(_.reduce(_ && _))
  }

  def setPhoneVerified(verified: Boolean) = {
    setFlag(PhoneVerifiedProp, verified)
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

  val loginAttributeProps = Array[Prop](PhoneProp, PhoneVerifiedProp, NameProp, LastLoginProp)
  def getLoginAttributes = {
    get(loginAttributeProps) map { props =>
      UserLoginAttributes(
        props(PhoneProp),
        props(PhoneVerifiedProp).map(Key.string2Boolean).contains(true),
        props(NameProp),
        props(LastLoginProp).map(TimeService().fromString))
    }
  }

}

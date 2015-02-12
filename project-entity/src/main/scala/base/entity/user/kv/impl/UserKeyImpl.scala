/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.facebook.{ FacebookInfo, FacebookService }
import base.entity.kv.Key._
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.impl.HashKeyImpl
import base.entity.user.kv.UserKeyProps._
import base.entity.user.kv.{ UserKey, UserLoginAttributes, UserNameAttributes, UserPhoneAttributes }
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserKeyImpl(val keyValue: UUID)
    extends HashKeyImpl[UUID]
    with UserKey {

  def getNameAttributes = {
    mGetAsMap(NameProp, FacebookIdProp) map { props =>
      UserNameAttributes(
        props.get(FacebookIdProp).map(bytea => FacebookService().getPictureUrl(read[String](bytea))),
        props.get(NameProp).map(read[String]))
    }
  }

  def getPhoneAttributes = {
    mGetAsMap(PhoneProp, PhoneCodeProp, PhoneVerifiedProp).map { props =>
      (props.get(PhoneProp).map(read[String]),
        props.get(PhoneCodeProp).map(read[String]),
        props.get(PhoneVerifiedProp).map(read[Boolean])) match {
          case (Some(phone), Some(code), Some(verified)) => Option(UserPhoneAttributes(phone, code, verified))
          case _                                         => None
        }
    }
  }

  def setPhoneAttributes(attributes: UserPhoneAttributes) = {
    val setCreated = setNX(CreatedProp, write(TimeService().now))
    val setPhone = set(PhoneProp, write(attributes.phone))
    val setVerified = setPhoneVerified(attributes.verified)
    val setCode = set(PhoneCodeProp, write(attributes.code))
    Future.sequence(Set(setCreated, setPhone, setVerified, setCode)).map(x => Unit)
  }

  def setPhoneVerified(verified: Boolean) = {
    set(PhoneVerifiedProp, write(verified)).map(x => Unit)
  }

  def setFacebookInfo(fbInfo: FacebookInfo) = {
    val props = Map[Prop, Array[Byte]](
      NameProp -> write(fbInfo.firstName),
      GenderProp -> write(fbInfo.gender),
      FacebookIdProp -> write(fbInfo.id),
      LocaleProp -> write(fbInfo.locale),
      UpdatedProp -> write(TimeService().now))
    mSet(props)
  }

  def getFacebookId = get(FacebookIdProp).map(_.map(read[String]))

  def getLocale = get(LocaleProp).map(_.map(read[String]))

  def getLastLogin = get(LastLoginProp).map(_.map(read[DateTime]))
  def setLastLogin(time: DateTime) = set(LastLoginProp, write(time)).map(x => Unit)

  def getLoginAttributes = {
    mGetAsMap(PhoneProp, PhoneVerifiedProp, FacebookIdProp, NameProp, LastLoginProp) map { props =>
      UserLoginAttributes(
        props.get(PhoneProp).map(read[String]),
        props.get(PhoneVerifiedProp).map(read[Boolean]).contains(true),
        UserNameAttributes(
          props.get(FacebookIdProp).map(bytea => FacebookService().getPictureUrl(read[String](bytea))),
          props.get(NameProp).map(read[String])),
        props.get(LastLoginProp).map(read[DateTime]))
    }
  }

  def getCreated = get(CreatedProp).map(_.map(read[DateTime]))
  def getUpdated = get(UpdatedProp).map(_.map(read[DateTime]))

}

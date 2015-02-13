/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:52 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.time.TimeService
import base.entity.facebook.{ FacebookInfo, FacebookService }
import base.entity.kv.serializer.SerializerImplicits._
import base.entity.user.kv.impl.UserKeyImpl._
import base.entity.user.kv.{ UserKey, UserLoginAttributes, UserNameAttributes, UserPhoneAttributes }
import org.joda.time.DateTime
import scredis.keys.{ HashKey, HashKeyProp, HashKeyProps }
import scredis.serialization.Implicits._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserKeyImpl(keyFactory: HashKeyProps => HashKey[Short, UUID])
    extends UserKey
    with Dispatchable {

  private lazy val key = keyFactory(props)

  def getNameAttributes = {
    key.mGetAsMap[Array[Byte]](NameProp, FacebookIdProp) map { props =>
      UserNameAttributes(
        props.get(FacebookIdProp).map(bytea => FacebookService().getPictureUrl(stringReader.read(bytea))),
        props.get(NameProp).map(stringReader.read))
    }
  }

  def getPhoneAttributes = {
    key.mGetAsMap[Array[Byte]](PhoneProp, PhoneCodeProp, PhoneVerifiedProp).map { props =>
      (props.get(PhoneProp).map(stringReader.read),
        props.get(PhoneCodeProp).map(stringReader.read),
        props.get(PhoneVerifiedProp).map(booleanReader.read)) match {
          case (Some(phone), Some(code), Some(verified)) => Option(UserPhoneAttributes(phone, code, verified))
          case _                                         => None
        }
    }
  }

  def setPhoneAttributes(attributes: UserPhoneAttributes) = {
    val setCreated = key.setNX(CreatedProp, TimeService().now)
    val setPhone = key.set(PhoneProp, attributes.phone)
    val setVerified = setPhoneVerified(attributes.verified)
    val setCode = key.set(PhoneCodeProp, attributes.code)
    Future.sequence(Set(setCreated, setPhone, setVerified, setCode)).map(x => Unit)
  }

  def setPhoneVerified(verified: Boolean) = key.set(PhoneVerifiedProp, verified).map(x => Unit)

  def setFacebookInfo(fbInfo: FacebookInfo) = {
    val props = Map[HashKeyProp, Array[Byte]](
      NameProp -> stringWriter.write(fbInfo.firstName),
      GenderProp -> stringWriter.write(fbInfo.gender),
      FacebookIdProp -> stringWriter.write(fbInfo.id),
      LocaleProp -> stringWriter.write(fbInfo.locale),
      UpdatedProp -> dateTimeSerializer.write(TimeService().now))
    key.mSet(props)
  }

  def getFacebookId = key.get[String](FacebookIdProp)

  def getLocale = key.get[String](LocaleProp)

  def getLastLogin = key.get[DateTime](LastLoginProp)
  def setLastLogin(time: DateTime) = key.set(LastLoginProp, time).map(x => Unit)

  def getLoginAttributes = {
    key.mGetAsMap[Array[Byte]](PhoneProp, PhoneVerifiedProp, FacebookIdProp, NameProp, LastLoginProp) map { props =>
      UserLoginAttributes(
        props.get(PhoneProp).map(stringReader.read),
        props.get(PhoneVerifiedProp).map(booleanReader.read).contains(true),
        UserNameAttributes(
          props.get(FacebookIdProp).map(bytea => FacebookService().getPictureUrl(stringReader.read(bytea))),
          props.get(NameProp).map(stringReader.read)),
        props.get(LastLoginProp).map(dateTimeSerializer.read))
    }
  }

  def getCreated = key.get[DateTime](CreatedProp)
  def getUpdated = key.get[DateTime](UpdatedProp)

}

private[impl] object UserKeyImpl {

  val CreatedProp = HashKeyProp("created")
  val UpdatedProp = HashKeyProp("updated")
  val NameProp = HashKeyProp("name")
  val PhoneProp = HashKeyProp("phone")
  val PhoneCodeProp = HashKeyProp("phoneCode")
  val LastLoginProp = HashKeyProp("lastLogin")
  val LocaleProp = HashKeyProp("locale")
  val GenderProp = HashKeyProp("gender")
  val PhoneVerifiedProp = HashKeyProp("phoneVerified")
  val FacebookIdProp = HashKeyProp("facebookId")

  val props = HashKeyProps(Set(
    CreatedProp,
    UpdatedProp,
    NameProp,
    PhoneProp,
    PhoneCodeProp,
    LastLoginProp,
    LocaleProp,
    GenderProp,
    PhoneVerifiedProp,
    FacebookIdProp))

}

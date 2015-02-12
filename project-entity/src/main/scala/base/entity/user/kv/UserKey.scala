/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.user.kv

import java.util.UUID

import base.common.time.TimeService
import base.entity.facebook.FacebookInfo
import base.entity.kv.{ HashKey, KeyPrefixes }
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait UserKey extends HashKey[UUID] {

  final val keyPrefix = KeyPrefixes.user

  def getNameAttributes: Future[UserNameAttributes]

  def getPhoneAttributes: Future[Option[UserPhoneAttributes]]
  def setPhoneAttributes(attributes: UserPhoneAttributes): Future[Unit]
  def setPhoneVerified(verified: Boolean): Future[Unit]

  def getFacebookId: Future[Option[String]]
  def setFacebookInfo(fbInfo: FacebookInfo): Future[Unit]

  def getLocale: Future[Option[String]]

  def getLastLogin: Future[Option[DateTime]]
  def setLastLogin(time: DateTime = TimeService().now): Future[Unit]

  def getLoginAttributes: Future[UserLoginAttributes]

  def getCreated: Future[Option[DateTime]]
  def getUpdated: Future[Option[DateTime]]

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 4:02 PM
 */

package base.entity.user.kv

import base.common.time.TimeService
import base.entity.facebook.FacebookInfo
import base.entity.kv.HashKey
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait UserKey extends HashKey {

  def getNameAttributes: Future[UserNameAttributes]

  def getPhoneAttributes: Future[Option[UserPhoneAttributes]]
  def setPhoneAttributes(attributes: UserPhoneAttributes): Future[Boolean]
  def setPhoneVerified(verified: Boolean): Future[Boolean]

  def getFacebookId: Future[Option[String]]
  def setFacebookInfo(fbInfo: FacebookInfo): Future[Boolean]

  def getLocale: Future[Option[String]]

  def getLastLogin: Future[Option[DateTime]]
  def setLastLogin(time: DateTime = TimeService().now): Future[Boolean]

  def getLoginAttributes: Future[UserLoginAttributes]

}

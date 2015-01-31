/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/27/15 7:44 PM
 */

package base.entity.user.kv

import base.common.lib.Genders.Gender
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

  def getNameAndGender: Future[(Option[String], Option[Gender])]

  def getPhoneVerified: Future[(Option[String], Option[Boolean])]
  def setPhoneVerified(phone: String, verified: Boolean): Future[Boolean]

  def getFacebookId: Future[Option[String]]
  def setFacebookInfo(fbInfo: FacebookInfo): Future[Boolean]

  def getLocale: Future[Option[String]]

  def getLastLogin: Future[Option[DateTime]]
  def setLastLogin(time: DateTime = TimeService().now): Future[Boolean]

}

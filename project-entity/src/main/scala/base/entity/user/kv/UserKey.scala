/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:15 PM
 */

package base.entity.user.kv

import base.common.lib.Genders.Gender
import base.common.time.TimeService
import base.entity.kv.HashKey
import base.entity.kv.Key._
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait UserKey extends HashKey {

  def getNameAndGender(implicit p: Pipeline): Future[(Option[String], Option[Gender])]

  def setNameAndGender(name: String, gender: Gender)(implicit p: Pipeline): Future[Boolean]

  def getLastLogin(implicit p: Pipeline): Future[Option[DateTime]]
  def setLastLogin(time: DateTime = TimeService().now)(implicit p: Pipeline): Future[Boolean]

}

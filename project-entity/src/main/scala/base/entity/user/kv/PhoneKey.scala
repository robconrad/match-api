/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:15 PM
 */

package base.entity.user.kv

import java.util.UUID

import base.entity.kv.HashKey
import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait PhoneKey extends HashKey {

  def getCode(implicit p: Pipeline): Future[Option[String]]
  def setCode(code: String)(implicit p: Pipeline): Future[Boolean]

  def getUserId(implicit p: Pipeline): Future[Option[UUID]]
  def setUserId(userId: UUID)(implicit p: Pipeline): Future[Boolean]

}

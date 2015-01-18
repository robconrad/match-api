/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
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

  def getCode: Future[Option[String]]
  def setCode(code: String): Future[Boolean]

  def getUserId: Future[Option[UUID]]
  def setUserId(userId: UUID): Future[Boolean]

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.group.kv

import java.util.UUID

import base.common.time.TimeService
import base.entity.kv.{ KeyPrefixes, HashKey }
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupKey extends HashKey[UUID] {

  final val keyPrefix = KeyPrefixes.group

  def create(): Future[Boolean]

  def getCreated: Future[Option[DateTime]]

  def getLastEventAndCount: Future[(Option[DateTime], Option[Int])]

  def setLastEvent(time: DateTime = TimeService().now): Future[Boolean]

  def setEventCount(count: Int): Future[Boolean]

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.group.kv

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
trait GroupKey extends HashKey {

  def getLastEventAndCount: Future[(Option[DateTime], Option[Int])]

  def setLastEvent(time: DateTime = TimeService().now): Future[Boolean]

  def setEventCount(count: Int): Future[Boolean]

}

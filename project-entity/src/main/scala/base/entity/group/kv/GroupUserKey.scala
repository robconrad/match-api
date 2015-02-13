/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:53 PM
 */

package base.entity.group.kv

import base.common.time.TimeService
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupUserKey {

  def getLastRead: Future[Option[DateTime]]
  def setLastRead(time: DateTime = TimeService().now): Future[Boolean]

}

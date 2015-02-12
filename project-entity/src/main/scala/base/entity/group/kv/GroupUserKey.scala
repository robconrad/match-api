/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.group.kv

import base.common.time.TimeService
import base.entity.kv.{ OrderedIdPair, HashKey, KeyPrefixes }
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupUserKey extends HashKey[OrderedIdPair] {

  final val keyPrefix = KeyPrefixes.groupUser

  def getLastRead: Future[Option[DateTime]]
  def setLastRead(time: DateTime = TimeService().now): Future[Boolean]

}

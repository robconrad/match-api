/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.group.kv.GroupKey
import base.entity.group.kv.GroupKeyProps.{ EventCountProp, LastEventTimeProp }
import base.entity.kv.KeyProps.CreatedProp
import base.entity.kv.impl.HashKeyImpl
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupKeyImpl(val keyValue: UUID)
    extends HashKeyImpl[UUID]
    with GroupKey {

  def create() = setNX(CreatedProp, write(TimeService().now))

  def getCreated = get(CreatedProp).map(_.map(read[DateTime]))

  def getLastEventAndCount: Future[(Option[DateTime], Option[Int])] = {
    mGetAsMap(LastEventTimeProp, EventCountProp).map { props =>
      val time = props.get(LastEventTimeProp).map(read[DateTime])
      val count = props.get(EventCountProp).map(read[Int])
      (time, count)
    }
  }

  def setLastEvent(time: DateTime) =
    set(LastEventTimeProp, write(TimeService().now))

  def setEventCount(count: Int) =
    set(EventCountProp, write(count))

}

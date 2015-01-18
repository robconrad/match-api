/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:33 PM
 */

package base.entity.group.kv.impl

import base.common.time.TimeService
import base.entity.group.kv.GroupKey
import base.entity.group.kv.GroupKeyProps.{ EventCountProp, LastEventTimeProp }
import base.entity.kv.Key._
import base.entity.kv.PrivateHashKey
import base.entity.kv.impl.HashKeyImpl
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupKeyImpl(protected val key: PrivateHashKey)(implicit protected val p: Pipeline)
    extends GroupKey with HashKeyImpl {

  private val props = Array[Prop](LastEventTimeProp, EventCountProp)
  def getLastEventAndCount: Future[(Option[DateTime], Option[Int])] = {
    key.get(props).map { props =>
      val time = props(LastEventTimeProp).map(TimeService().fromString)
      val count = props(EventCountProp).map(_.toInt)
      (time, count)
    }
  }

  def setLastEvent(time: DateTime) =
    key.set(LastEventTimeProp, TimeService().asString(time))

  def setEventCount(count: Int) =
    key.set(EventCountProp, count)

}

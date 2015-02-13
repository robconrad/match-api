/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:52 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.time.TimeService
import base.entity.group.kv.GroupKey
import base.entity.group.kv.impl.GroupKeyImpl._
import base.entity.kv.serializer.SerializerImplicits._
import org.joda.time.DateTime
import scredis.keys.{ HashKey, HashKeyProp, HashKeyProps }
import scredis.serialization.Implicits._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupKeyImpl(keyFactory: HashKeyProps => HashKey[Short, UUID])
    extends GroupKey
    with Dispatchable {

  private lazy val key = keyFactory(props)

  def create() = key.setNX[DateTime](CreatedProp, TimeService().now)

  def getCreated = key.get[DateTime](CreatedProp)

  def getLastEventAndCount: Future[(Option[DateTime], Option[Int])] = {
    key.mGetAsMap[Array[Byte]](LastEventTimeProp, EventCountProp).map { props =>
      val time = props.get(LastEventTimeProp).map(dateTimeSerializer.read)
      val count = props.get(EventCountProp).map(intReader.read)
      (time, count)
    }
  }

  def setLastEvent(time: DateTime) =
    key.set(LastEventTimeProp, TimeService().now)

  def setEventCount(count: Int) =
    key.set(EventCountProp, count)

}

private[impl] object GroupKeyImpl {

  val CreatedProp = HashKeyProp("created")
  val LastEventTimeProp = HashKeyProp("lastEvent")
  val EventCountProp = HashKeyProp("eventCount")

  val props = HashKeyProps(Set(CreatedProp, LastEventTimeProp, EventCountProp))

}

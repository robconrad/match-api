/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:52 PM
 */

package base.entity.group.kv.impl

import base.common.lib.Dispatchable
import base.entity.group.kv.GroupUserKey
import base.entity.group.kv.impl.GroupUserKeyImpl._
import base.entity.kv.OrderedIdPair
import base.entity.kv.serializer.SerializerImplicits._
import org.joda.time.DateTime
import scredis.keys.{ HashKey, HashKeyProp, HashKeyProps }

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserKeyImpl(keyFactory: HashKeyProps => HashKey[Short, OrderedIdPair])
    extends GroupUserKey
    with Dispatchable {

  private lazy val key = keyFactory(props)

  def getLastRead = key.get[DateTime](LastReadTimeProp)
  def setLastRead(time: DateTime) = key.set(LastReadTimeProp, time)

}

private[impl] object GroupUserKeyImpl {

  val LastReadTimeProp = HashKeyProp("lastRead")

  val props = HashKeyProps(Set(LastReadTimeProp))

}

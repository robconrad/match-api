/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:30 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.GroupKeyProps.LastReadTimeProp
import base.entity.group.kv.GroupUserKey
import base.entity.kv.OrderedIdPair
import base.entity.kv.impl.HashKeyImpl
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserKeyImpl(val keyValue: OrderedIdPair)
    extends HashKeyImpl[OrderedIdPair]
    with GroupUserKey {

  def this(keyValue: (UUID, UUID)) =
    this(OrderedIdPair(keyValue._1, keyValue._2))

  def getLastRead = get(LastReadTimeProp).map(_.map(read[DateTime]))
  def setLastRead(time: DateTime) = set(LastReadTimeProp, write(time))

}

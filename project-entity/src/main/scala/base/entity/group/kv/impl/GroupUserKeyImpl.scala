/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 4:28 PM
 */

package base.entity.group.kv.impl

import base.common.time.TimeService
import base.entity.group.kv.GroupKeyProps.LastReadTimeProp
import base.entity.group.kv.GroupUserKey
import base.entity.kv.Key.Pipeline
import base.entity.kv.PrivateHashKey
import base.entity.kv.impl.HashKeyImpl
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserKeyImpl(protected val key: PrivateHashKey) extends GroupUserKey with HashKeyImpl {

  def getLastRead(implicit p: Pipeline) = key.getDateTime(LastReadTimeProp)
  def setLastRead(time: DateTime)(implicit p: Pipeline) = key.set(LastReadTimeProp, TimeService().asString(time))

}

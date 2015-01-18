/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 4:21 PM
 */

package base.entity.group.kv

import base.entity.kv.KeyProp

/**
 * {{ Describe the high level purpose of UserKeyProps here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[group] object GroupKeyProps {

  // group shiz
  object EventCountProp extends KeyProp("eventCount")
  object LastEventTimeProp extends KeyProp("lastEventTime")

  // group user shiz
  object LastReadTimeProp extends KeyProp("lastReadTime")

}

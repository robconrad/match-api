/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 7:43 PM
 */

package base.entity.event

/**
 * {{ Describe the high level purpose of EventTypes here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object EventTypes extends Enumeration {
  type EventType = Value

  val MATCH = Value("match")
  val PAIRING = Value("pairing")
  val MESSAGE = Value("msg")

}

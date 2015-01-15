/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:10 PM
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

  val JOIN = Value("join")
  val MATCH = Value("match")
  val MESSAGE = Value("msg")

}

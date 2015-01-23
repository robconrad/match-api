/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:30 PM
 */

package base.entity.kv

import java.nio.ByteBuffer

/**
 * {{ Describe the high level purpose of KeyPrefixes here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off magic.number
object KeyPrefixes extends Enumeration {
  type KeyPrefix = Value

  implicit class Props(v: Value) {
    def toBytes = ByteBuffer.allocate(2).putShort(v.id.toShort).array()
  }

  /**
   * WARNING: DANGER: VERY IMPORTANT: these can never change value or the kv store is hosed!
   * WARNING: DANGER: VERY IMPORTANT: the byte array produces is always 2 bytes long: max id of 65536
   */

  // format: OFF
  val device                    = Value(10000)
  val group                     = Value(20000)
  val groupEvents               = Value(21000)
  val groupPair                 = Value(22000)
  val groupUser                 = Value(23000)
  val groupUserQuestion         = Value(24000)
  val groupUserQuestions        = Value(25000)
  val groupUserQuestionsTemp    = Value(26000)
  val groupUserQuestionsYes     = Value(27000)
  val groupUsers                = Value(28000)
  val phone                     = Value(30000)
  val phoneCooldown             = Value(31000)
  val questions                 = Value(40000)
  val test                      = Value(1000)
  val user                      = Value(50000)
  val userGroups                = Value(51000)
  val userUserLabel             = Value(52000)
  // format: ON

}

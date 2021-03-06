/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:06 PM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of KeyPrefixes here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off magic.number
object KeyPrefixes extends Enumeration {
  type KeyPrefix = Value

  implicit def toShort(v: Value): Short = v.id.toShort

  /**
   * WARNING: DANGER: VERY IMPORTANT: these can never change value or the kv store is hosed!
   * WARNING: DANGER: VERY IMPORTANT: the byte array produces is always 2 bytes long: max id of 32,768
   */

  // format: OFF
  val device                    = Value(10000)
  val facebookInfo              = Value(11000)
  val facebookUser              = Value(12000)
  val group                     = Value(13000)
  val groupEvents               = Value(14000)
  val groupPhonesInvited        = Value(15000)
  val groupQuestions            = Value(15500)
  val groupUser                 = Value(16000)
  val groupUserQuestions        = Value(17000)
  val groupUserQuestionsTemp    = Value(18000)
  val groupUserQuestionsYes     = Value(19000)
  val groupUsers                = Value(20000)
  val phone                     = Value(21000)
  val phoneCooldown             = Value(22000)
  val phoneGroupsInvited        = Value(23000)
  val question                  = Value(23500)
  val questions                 = Value(24000)
  val test                      = Value(25000)
  val user                      = Value(26000)
  val userGroups                = Value(27000)
  val userGroupsInvited         = Value(28000)
  val userPhoneLabel            = Value(29000)
  val userPhonesInvited         = Value(30000)
  val userQuestions             = Value(31000)
  // format: ON

}

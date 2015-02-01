/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 2:25 PM
 */

package base.entity.perm

/**
 * Logical groupings of permissions that are often granted together (e.g. READ_ONE and READ_MANY could be
 *  granted together as the set READ)
 * @author rconrad
 */
private[perm] object PermSets {

  val login = PermSet(
    Perms.LOGIN
  )

  val phone = PermSet(
    Perms.REGISTER_PHONE,
    Perms.VERIFY_PHONE
  )

  val invite = PermSet(
    Perms.INVITE,
    Perms.ACCEPT_INVITE,
    Perms.DECLINE_INVITE
  )

  val message = PermSet(
    Perms.MESSAGE
  )

  val questions = PermSet(
    Perms.QUESTIONS,
    Perms.ANSWER
  )

}

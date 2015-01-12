/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 4:03 PM
 */

package base.entity.perm

/**
 * Logical groupings of permissions that are often granted together (e.g. READ_ONE and READ_MANY could be
 *  granted together as the set READ)
 * @author rconrad
 */
private[perm] object PermSets {

  val register = PermSet(
    Perms.REGISTER
  )

  val verify = PermSet(
    Perms.VERIFY
  )

  val login = PermSet(
    Perms.LOGIN
  )

}

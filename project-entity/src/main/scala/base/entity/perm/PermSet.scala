/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.perm

import base.entity.perm.Perms.Perm

/**
 * Logical groupings of permissions that are often granted together (e.g. READ_ONE and READ_MANY could be
 *  granted together as the set READ)
 * @author rconrad
 */
case class PermSet private[perm] (set: Set[Perm]) {

  def ++(other: PermSet) = PermSet(this.set ++ other.set)
  def contains(p: Perm) = set.contains(p)

}

object PermSet {

  private[perm] def apply(permissions: Perm*): PermSet = apply(permissions.toSet)

}

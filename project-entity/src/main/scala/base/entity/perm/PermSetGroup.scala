/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.perm

import base.entity.perm.Perms.Perm

/**
 * Collections of PermSets that are granted to particular profiles of AuthContexts.
 * @author rconrad
 */
case class PermSetGroup private[perm] (permSet: PermSet) {

  def contains(p: Perm) = permSet.contains(p)
  def ++(other: PermSetGroup) = PermSetGroup(this.permSet ++ other.permSet)
  def -(p: Perm) = PermSetGroup(PermSet(permSet.set.filter(_ != p)))

}

object PermSetGroup {

  private[perm] def apply(set: Set[PermSet]): PermSetGroup = apply(set.reduce(_ ++ _))

  private[perm] def sets(sets: PermSet*): PermSetGroup = apply(sets.toSet)
  private[perm] def groups(groups: PermSetGroup*): PermSetGroup = apply(groups.reduce(_ ++ _).permSet)

}

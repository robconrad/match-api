/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:08 AM
 */

package base.entity.auth.context

import base.entity.Tables.UserRow
import base.entity.auth.AuthRoles
import base.entity.perm.PermSetGroups

/**
 * {{ Describe the high level purpose of StandardUserAuthContext here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class StandardUserAuthContext(_user: UserRow) extends UserAuthContext {

  val user = Option(_user)

  val perms = PermSetGroups.user
  val authRole = AuthRoles.PRIVILEGED

}


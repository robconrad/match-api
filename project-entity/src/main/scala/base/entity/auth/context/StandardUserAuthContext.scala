/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:59 PM
 */

package base.entity.auth.context

import base.entity.Tables.UserRow
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

}


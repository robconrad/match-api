/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 7:01 PM
 */

package base.entity.auth.context

import java.util.UUID

import base.entity.perm.PermSetGroups
import base.entity.user.User

/**
 * {{ Describe the high level purpose of StandardUserAuthContext here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class StandardUserAuthContext(_user: User, groups: Set[UUID]) extends UserAuthContext {

  val user = Option(_user)

  val perms = PermSetGroups.user

}


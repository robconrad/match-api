/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 10:19 AM
 */

package base.entity.auth.context

import base.entity.perm.PermSetGroups
import base.entity.user.User

/**
 * Creates various fake auth contexts
 * @author rconrad
 */
object AuthContextDataFactory {

  val emptyUserAuth = new UserAuthContext {
    val user = None
    val perms = PermSetGroups.user
  }

  val userAuth = {
    val userId = 123
    new StandardUserAuthContext(User(userId))
  }

}

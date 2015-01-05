/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 10:01 PM
 */

package base.entity.auth.context

import base.entity.perm.PermSetGroups
import base.entity.user.UserDataFactory

/**
 * Creates various fake auth contexts
 * @author rconrad
 */
object AuthContextDataFactory {

  val emptyUserAuth = new UserAuthContext {
    val user = Option(UserDataFactory.makeRow())
    val perms = PermSetGroups.god
  }

}

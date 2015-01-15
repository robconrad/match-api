/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:52 AM
 */

package base.entity.auth.context

import base.common.random.RandomService
import base.entity.perm.PermSetGroups
import base.entity.user.User

/**
 * Creates various fake auth contexts
 * @author rconrad
 */
object AuthContextDataFactory {

  val emptyUserAuth = new UserAuthContext {
    val user = None
    val perms = PermSetGroups.none
  }

  val userAuth = new StandardUserAuthContext(User(RandomService().uuid))

}

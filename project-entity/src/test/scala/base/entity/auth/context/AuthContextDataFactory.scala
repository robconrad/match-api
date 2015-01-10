/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 8:36 PM
 */

package base.entity.auth.context

import base.entity.perm.PermSetGroups

/**
 * Creates various fake auth contexts
 * @author rconrad
 */
object AuthContextDataFactory {

  val emptyUserAuth = new UserAuthContext {
    val user = None
    val perms = PermSetGroups.public
  }

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:25 AM
 */

package base.entity.auth.context

import base.entity.apiKey.ApiKeyDataFactory
import base.entity.auth.{ AuthRoles, AuthTypes }
import base.entity.perm.PermSetGroups
import base.entity.user.UserDataFactory

/**
 * Creates various fake auth contexts
 * @author rconrad
 */
object AuthContextDataFactory {

  // scalastyle:off null
  val emptyKeyAuth = {
    new KeyAuthContext with AuthContext {
      val authKey = ApiKeyDataFactory.makeRow()
      val authRole = AuthRoles.PUBLIC
      val perms = PermSetGroups.god
    }
  }

  val emptyUserAuth = new UserAuthContext {
    val user = Option(UserDataFactory.makeRow())
    val authRole = AuthRoles.PUBLIC
    val perms = PermSetGroups.god
  }

}

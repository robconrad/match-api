/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.auth.context

import base.entity.auth.{ AuthRoles, AuthTypes }
import base.entity.perm.PermSetGroups

/**
 * Public AuthContext has essentially no attributes but is used for determining what
 *  perms a non-authed request will have
 * @author rconrad
 */
case object NoAuthContext extends AuthContext {

  val perms = PermSetGroups.public

  val merchant = None
  val provider = None
  val site = None
  val user = None

  val authRole = AuthRoles.PUBLIC
  val authType = AuthTypes.PUBLIC
  val authTypeId = None

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:59 PM
 */

package base.entity.auth.context

import base.entity.perm.PermSetGroups

/**
 * Public AuthContext has essentially no attributes but is used for determining what
 *  perms a non-authed request will have
 * @author rconrad
 */
case object NoAuthContext extends AuthContext {

  val perms = PermSetGroups.public

  val user = None

  val authTypeId = None

}

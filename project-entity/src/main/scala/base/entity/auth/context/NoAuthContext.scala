/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 6:32 PM
 */

package base.entity.auth.context

import java.util.UUID

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

  val groups = Set[UUID]()

}

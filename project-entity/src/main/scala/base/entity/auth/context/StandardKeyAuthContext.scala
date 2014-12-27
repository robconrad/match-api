/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:46 PM
 */

package base.entity.auth.context

import base.entity.Tables.ApiKeyRow
import base.entity.auth.AuthRoles
import base.entity.perm.PermSetGroups

/**
 * {{ Describe the high level purpose of StandardKeyAuthContext here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class StandardKeyAuthContext(authKey: ApiKeyRow) extends KeyAuthContext {

  val perms = PermSetGroups.user
  val authRole = AuthRoles.PRIVILEGED

}

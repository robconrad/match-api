/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.auth.context

import base.entity.auth.AuthTypes

/**
 * User-specific auth context, contains the User information of the context user along with global context
 * @author rconrad
 */
trait UserAuthContext extends AuthContext {

  final lazy val authType = AuthTypes.USER
  final lazy val authTypeId = Option(utilities.userId)

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:59 PM
 */

package base.entity.auth.context

/**
 * User-specific auth context, contains the User information of the context user along with global context
 * @author rconrad
 */
trait UserAuthContext extends AuthContext {

  final lazy val authTypeId = Option(utilities.userId)

}

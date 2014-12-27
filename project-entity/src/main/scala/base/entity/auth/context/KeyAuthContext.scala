/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:25 AM
 */

package base.entity.auth.context

import base.entity.Tables.ApiKeyRow
import base.entity.auth.AuthTypes

/**
 * Key-specific auth context, contains the Key information of the context "user" along with global context
 * @author rconrad
 */
trait KeyAuthContext extends AuthContext {

  // TODO why is user an optional on AuthContext but authKey is not?
  val authKey: ApiKeyRow

  final val authType = AuthTypes.KEY

  final val user = None

  final lazy val authTypeId = Option(authKey.id)

  final override def assertIsValid() {

  }

}

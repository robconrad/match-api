/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.auth

import base.entity.auth.AuthTypes.AuthType

/**
 * Contains a set of allowed authentication types that may be used to authenticate with a given endpoint
 * @author rconrad
 */
case class AuthTypeSet private[auth] (set: Set[AuthType]) {

  def contains(authTypes: AuthType*) = authTypes.map(set.contains).reduce(_ || _)
  def contains(authType: Option[AuthType]) = authType.exists(set.contains)

}

object AuthTypeSet {

  private[auth] def apply(types: AuthType*): AuthTypeSet = apply(types.toSet)

}

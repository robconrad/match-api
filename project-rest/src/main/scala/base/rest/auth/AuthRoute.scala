/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.auth

import base.entity.auth.AuthTypeSets
import base.entity.auth.context.AuthContext
import base.entity.perm.Perms
import base.rest.Endpoint._
import base.rest.route.VersionedRestRoute
import scala.reflect.runtime.universe.typeTag

/**
 * API route description for auth endpoint
 * @author rconrad
 */
private[rest] trait AuthRoute extends VersionedRestRoute {

  def endpoints = List(AUTH)

  def restRoutes = read

  // purposely not documented in swagger
  def read =
    path(AUTH) {
      get {
        auth(AuthTypeSets.USER_OR_KEY, Perms.AUTH_READ) { authCtx =>
          completeEmpty
        }
      }
    }

}

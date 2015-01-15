/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:04 PM
 */

package base.entity.command

import base.common.service.Service
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.perm.Perms.Perm

import scala.concurrent.Future

/**
 * A Command is a combination of an HTTP Verb and an endpoint - PostFoo, GetBar, PatchBaz, etc.
 * @author rconrad
 */
trait CommandService[A, B] extends Service {

  def perms: Iterable[Perm]

  def execute(input: A)(implicit authCtx: AuthContext): Future[Either[ApiError, B]]

}

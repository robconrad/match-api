/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 9:49 AM
 */

package base.entity.user.impl

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.user.VerifyService
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class VerifyServiceImpl()
    extends ServiceImpl with VerifyService with Dispatchable with AuthLoggable {

  def verify(input: VerifyModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.VERIFY)
    Future.successful(Left(ApiError("not implemented")))
  }

}

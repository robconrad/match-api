/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:19 PM
 */

package base.entity.command.impl

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.command.CommandService
import base.entity.logging.AuthLoggable
import base.entity.service.CrudImplicits

/**
 * {{ Describe the high level purpose of CommandServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] trait CommandServiceImpl[A, B]
    extends ServiceImpl
    with CommandService[A, B]
    with CrudImplicits[B]
    with AuthLoggable {

  final def execute(input: A)(implicit authCtx: AuthContext) = {
    perms.foreach { perm =>
      authCtx.assertHas(perm)
    }
    innerExecute(input)
  }

  protected def innerExecute(input: A)(implicit authCtx: AuthContext): Response

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 11:59 PM
 */

package base.entity.command.impl

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.command.CommandService
import base.entity.command.model.CommandModel
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

  final def execute(input: A)(implicit channelCtx: ChannelContext) = {
    perms.foreach { perm =>
      channelCtx.authCtx.assertHas(perm)
    }
    innerExecute(input).map {
      case Right(_: Unit)  => None
      case Right(response) => Option(CommandModel(outCmd.get, response))
      case Left(error)     => Option(CommandModel(errorCmd, error))
    }
  }

  protected def innerExecute(input: A)(implicit channelCtx: ChannelContext): Response

}

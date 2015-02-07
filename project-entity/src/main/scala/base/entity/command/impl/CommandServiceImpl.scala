/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/5/15 8:03 PM
 */

package base.entity.command.impl

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.command.CommandService
import base.entity.command.model.CommandModel
import base.entity.error.model.ApiError
import base.entity.kv.MakeKey
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
    with MakeKey
    with AuthLoggable {

  protected val responseManifest: Option[Manifest[B]] = None
  private val errorManifest = manifest[ApiError]

  final def execute(input: A)(implicit channelCtx: ChannelContext) = {
    perms.foreach { perm =>
      channelCtx.authCtx.assertHas(perm)
    }
    innerExecute(input).map {
      case Right(response) =>
        responseManifest match {
          case Some(responseManifest) => Option(CommandModel(response)(responseManifest))
          case None                   => None
        }
      case Left(error) => Option(CommandModel(error)(errorManifest))
    }
  }

  protected def innerExecute(input: A)(implicit channelCtx: ChannelContext): Response

}

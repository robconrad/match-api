/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.command.impl

import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes
import base.entity.auth.context.ChannelContext
import base.entity.command.CommandService
import base.entity.command.model.{ CommandInputModel, CommandModel }
import base.entity.error.ApiErrorService
import base.entity.error.model.ApiError
import base.entity.kv.MakeKey
import base.entity.logging.AuthLoggable
import base.entity.service.CrudImplicits
import spray.http.StatusCodes

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of CommandServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] trait CommandServiceImpl[A <: CommandInputModel, B]
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
    input.assertGroupId.map(authCtx.hasGroup) match {
      case Some(false) => CommandServiceImpl.groupPermError
      case _ =>
        innerExecute(input).map {
          case Right(response) =>
            responseManifest match {
              case Some(responseManifest)              => Option(CommandModel(response)(responseManifest))
              case None if response.isInstanceOf[Unit] => None
              case None                                => throw new RuntimeException("you forgot to add a manifest")
            }
          case Left(error) => Option(CommandModel(error)(errorManifest))
        }
    }
  }

  protected def innerExecute(input: A)(implicit channelCtx: ChannelContext): Response

}

object CommandServiceImpl {

  val groupPermErrorText = "You are not a member of the requested group."
  val groupPermError = Future.successful(Option(CommandModel(ApiErrorService().errorCode(
    groupPermErrorText, StatusCodes.Unauthorized, ApiErrorCodes.NOT_GROUP_MEMBER))))

}

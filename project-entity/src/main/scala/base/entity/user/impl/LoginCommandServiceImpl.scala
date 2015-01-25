/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:19 AM
 */

package base.entity.user.impl

import java.util.UUID

import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.model.EventModel
import base.entity.group.model.GroupModel
import base.entity.group.{ GroupListenerService, GroupEventsService, UserService }
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel
import base.entity.service.CrudErrorImplicits
import base.entity.user._
import base.entity.user.impl.LoginCommandServiceImpl.Errors
import base.entity.user.kv.{ DeviceKey, DeviceKeyService, UserKey, UserKeyService }
import base.entity.user.model.{ LoginModel, LoginResponseModel }
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class LoginCommandServiceImpl()
    extends CommandServiceImpl[LoginModel, LoginResponseModel]
    with LoginCommandService {

  def innerExecute(input: LoginModel)(implicit channelCtx: ChannelContext) = {
    new LoginCommand(input).execute()
  }

  /**
   * - get device token
   * - validate provided token
   * - update device attributes
   * - get userId
   * - retrieve groups
   * - optionally retrieve current group events
   * - optionally retrieve current group questions
   * - update user attributes (last login)
   */
  private[impl] class LoginCommand(val input: LoginModel)(implicit val channelCtx: ChannelContext)
      extends Command[LoginModel, LoginResponseModel] {

    def execute(): Response = {
      deviceGetToken(DeviceKeyService().make(input.device.uuid))
    }

    def deviceGetToken(key: DeviceKey): Response = {
      key.getToken.flatMap {
        case Some(token) if input.token == token => deviceSet(key)
        case Some(token)                         => Errors.tokenInvalid
        case None                                => Errors.deviceUnverified
      }
    }

    def deviceSet(key: DeviceKey): Response = {
      key.set(
        input.appVersion,
        input.locale,
        input.device.model,
        input.device.cordova,
        input.device.platform,
        input.device.version
      ).flatMap {
          case true  => deviceGetUserId(key)
          case false => Errors.deviceSetFailed
        }
    }

    def deviceGetUserId(key: DeviceKey): Response = {
      key.getUserId.flatMap {
        case Some(userId) => groupsGet(userId)
        case None         => Errors.userIdGetFailed
      }
    }

    def groupsGet(userId: UUID): Response = {
      val key = UserKeyService().make(userId)
      UserService().getGroups(userId).flatMap {
        case Left(error) => error
        case Right(groups) => input.groupId match {
          case Some(groupId) => eventsGet(key, userId, groups, groupId)
          case None          => userGetSetLastLogin(key, userId, groups, None, None)
        }
      }
    }

    def eventsGet(key: UserKey,
                  userId: UUID,
                  groups: Iterable[GroupModel],
                  groupId: UUID): Response = {
      GroupEventsService().getEvents(groupId).flatMap {
        case Left(error) => error
        case Right(events) =>
          QuestionService().getQuestions(groupId).flatMap {
            case Left(error)      => error
            case Right(questions) => userGetSetLastLogin(key, userId, groups, Option(events), Option(questions))
          }
      }
    }

    def userGetSetLastLogin(key: UserKey,
                            userId: UUID,
                            groups: Iterable[GroupModel],
                            events: Option[List[EventModel]],
                            questions: Option[List[QuestionModel]]): Response = {
      key.getLastLogin.flatMap { lastLogin =>
        key.setLastLogin().flatMap {
          case true  => registerGroupListeners(LoginResponseModel(userId, groups.toList, events, questions, lastLogin))
          case false => Errors.userSetFailed
        }
      }
    }

    // todo test this
    def registerGroupListeners(response: LoginResponseModel): Response = {
      GroupListenerService().register(response.userId, response.groups.map(_.id).toSet) map { x =>
        response
      }
    }

  }

}

object LoginCommandServiceImpl {

  object Errors extends CrudErrorImplicits[LoginResponseModel] {

    override protected val externalErrorText = "There was a problem during login."

    private val deviceUnverifiedText = "This device has not been verified."
    private val tokenInvalidText = "The supplied token is not valid."

    lazy val deviceUnverified: Response = (deviceUnverifiedText, DEVICE_NOT_VERIFIED)
    lazy val tokenInvalid: Response = (tokenInvalidText, Unauthorized, TOKEN_NOT_VALID)
    lazy val deviceSetFailed: Response = "failed to set device attributes"
    lazy val userIdGetFailed: Response = "failed to get user id from device"
    lazy val userSetFailed: Response = "failed to set user attributes"

  }

}

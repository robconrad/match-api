/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:18 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.EventService
import base.entity.event.model.EventModel
import base.entity.kv._
import base.entity.group.GroupService
import base.entity.group.model.GroupModel
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel
import base.entity.service.CrudErrorImplicits
import base.entity.user._
import base.entity.user.impl.LoginCommandServiceImpl.Errors
import base.entity.user.kv.{ UserKeyService, UserKey, DeviceKeyService, DeviceKey }
import base.entity.user.model.{ LoginModel, LoginResponseModel }
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class LoginCommandServiceImpl()
    extends CommandServiceImpl[LoginModel, LoginResponseModel]
    with LoginCommandService {

  def innerExecute(input: LoginModel)(implicit authCtx: AuthContext) = {
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
  private[impl] class LoginCommand(val input: LoginModel) extends Command[LoginModel, LoginResponseModel] {

    def execute() = {
      deviceGetToken(DeviceKeyService().make(KeyId(input.device.uuid)))
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
      val key = UserKeyService().make(KeyId(userId))
      GroupService().getGroups(userId).flatMap {
        case Left(error) => error
        case Right(groups) => input.groupId match {
          case Some(groupId) => eventsGet(key, userId, groups, groupId)
          case None          => userGetSetLastLogin(key, userId, groups, None, None)
        }
      }
    }

    def eventsGet(key: UserKey,
                  userId: UUID,
                  groups: List[GroupModel],
                  groupId: UUID): Response = {
      EventService().getEvents(groupId).flatMap {
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
                            groups: List[GroupModel],
                            events: Option[List[EventModel]],
                            questions: Option[List[QuestionModel]]): Response = {
      key.getLastLogin.flatMap { lastLogin =>
        key.setLastLogin().flatMap {
          case true  => LoginResponseModel(userId, groups, events, questions, lastLogin)
          case false => Errors.userSetFailed
        }
      }
    }

  }

}

object LoginCommandServiceImpl {

  object Errors extends CrudErrorImplicits[LoginResponseModel] {

    protected val externalErrorText = "There was a problem during login."

    private val deviceUnverifiedText = "This device has not been verified."
    private val tokenInvalidText = "The supplied token is not valid."

    lazy val deviceUnverified: Response = (deviceUnverifiedText, DEVICE_NOT_VERIFIED)
    lazy val tokenInvalid: Response = (tokenInvalidText, Unauthorized, TOKEN_NOT_VALID)
    lazy val deviceSetFailed: Response = "failed to set device attributes"
    lazy val userIdGetFailed: Response = "failed to get user id from device"
    lazy val userSetFailed: Response = "failed to set user attributes"

  }

}

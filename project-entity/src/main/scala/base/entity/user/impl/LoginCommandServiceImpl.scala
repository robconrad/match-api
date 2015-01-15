/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:19 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.EventService
import base.entity.event.model.EventModel
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.pair.PairService
import base.entity.pair.model.PairModel
import base.entity.perm.Perms
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel
import base.entity.service.{ CrudErrorImplicits, CrudImplicits }
import base.entity.user._
import base.entity.user.impl.LoginCommandServiceImpl.Errors
import base.entity.user.model._
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
   * - retrieve pairs
   * - optionally retrieve current pair events
   * - optionally retrieve current pair questions
   * - update user attributes (last login)
   */
  private[impl] class LoginCommand(val input: LoginModel) extends Command[LoginModel, LoginResponseModel] {

    def execute() = {
      deviceGetToken(DeviceKeyService().make(KeyId(input.device.uuid)))
    }

    def deviceGetToken(deviceKey: DeviceKey): Response = {
      deviceKey.getToken.flatMap {
        case Some(token) if input.token == token => deviceSet(deviceKey)
        case Some(token)                         => Errors.tokenInvalid
        case None                                => Errors.deviceUnverified
      }
    }

    def deviceSet(deviceKey: DeviceKey): Response = {
      deviceKey.set(
        input.appVersion,
        input.locale,
        input.device.model,
        input.device.cordova,
        input.device.platform,
        input.device.version
      ).flatMap {
          case true  => deviceGetUserId(deviceKey)
          case false => Errors.deviceSetFailed
        }
    }

    def deviceGetUserId(deviceKey: DeviceKey): Response = {
      deviceKey.getUserId.flatMap {
        case Some(userId) => pairsGet(userId)
        case None         => Errors.userIdGetFailed
      }
    }

    def pairsGet(userId: UUID): Response = {
      val userKey = UserKeyService().make(KeyId(userId))
      PairService().getPairs(userId).flatMap {
        case Left(error) => error
        case Right(pairs) => input.pairId match {
          case Some(pairId) => eventsGet(userKey, userId, pairs, pairId)
          case None         => userGetSetLastLogin(userKey, userId, pairs, None, None)
        }
      }
    }

    def eventsGet(userKey: UserKey,
                  userId: UUID,
                  pairs: List[PairModel],
                  pairId: UUID): Response = {
      EventService().getEvents(pairId).flatMap {
        case Left(error) => error
        case Right(events) =>
          QuestionService().getQuestions(pairId).flatMap {
            case Left(error)      => error
            case Right(questions) => userGetSetLastLogin(userKey, userId, pairs, Option(events), Option(questions))
          }
      }
    }

    def userGetSetLastLogin(userKey: UserKey,
                            userId: UUID,
                            pairs: List[PairModel],
                            events: Option[List[EventModel]],
                            questions: Option[List[QuestionModel]]): Response = {
      userKey.getLastLogin.flatMap { lastLogin =>
        userKey.setLastLogin().flatMap {
          case true  => LoginResponseModel(userId, pairs, events, questions, lastLogin)
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

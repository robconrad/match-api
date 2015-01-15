/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:01 AM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.event.EventService
import base.entity.event.model.EventModel
import base.entity.kv.Key._
import base.entity.kv._
import base.entity.logging.AuthLoggable
import base.entity.pair.PairService
import base.entity.pair.model.PairModel
import base.entity.perm.Perms
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel
import base.entity.service.{ CrudErrorImplicits, CrudImplicits }
import base.entity.user.LoginCommandService.LoginResponse
import base.entity.user._
import base.entity.user.impl.LoginCommandServiceImpl.Errors
import base.entity.user.model._
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class LoginCommandServiceImpl()
    extends ServiceImpl
    with LoginCommandService
    with CrudImplicits[LoginResponseModel]
    with Dispatchable
    with AuthLoggable {

  private implicit val ec = dispatcher

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
  def login(input: LoginModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.LOGIN)
    deviceKeyGet(DeviceKeyService().make(KeyId(input.device.uuid)))(input, KvFactoryService().pipeline)
  }

  private[impl] def deviceKeyGet(deviceKey: DeviceKey)(implicit input: LoginModel,
                                                       p: Pipeline): LoginResponse = {
    deviceKey.getToken.flatMap {
      case Some(token) if input.token == token => deviceKeySet(deviceKey)
      case Some(token)                         => Errors.tokenInvalid
      case None                                => Errors.deviceUnverified
    }
  }

  private[impl] def deviceKeySet(deviceKey: DeviceKey)(implicit input: LoginModel,
                                                       p: Pipeline): LoginResponse = {
    deviceKey.set(
      input.appVersion,
      input.locale,
      input.device.model,
      input.device.cordova,
      input.device.platform,
      input.device.version
    ).flatMap {
        case true  => userKeyGet(deviceKey)
        case false => Errors.deviceSetFailed
      }
  }

  private[impl] def userKeyGet(deviceKey: DeviceKey)(implicit input: LoginModel,
                                                     p: Pipeline): LoginResponse = {
    deviceKey.getUserId.flatMap {
      case Some(userId) => getPairs(userId)
      case None         => Errors.userIdGetFailed
    }
  }

  private[impl] def getPairs(userId: UUID)(implicit input: LoginModel,
                                           p: Pipeline): LoginResponse = {
    val userKey = UserKeyService().make(KeyId(userId))
    PairService().getPairs(userId).flatMap {
      case Left(error) => error
      case Right(pairs) => input.pairId match {
        case Some(pairId) => getPairEvents(userKey, userId, pairs, pairId)
        case None         => userKeyGetLogin(userKey, userId, pairs, None, None)
      }
    }
  }

  private[impl] def getPairEvents(userKey: UserKey,
                                  userId: UUID,
                                  pairs: List[PairModel],
                                  pairId: UUID)(implicit input: LoginModel,
                                                p: Pipeline): LoginResponse = {
    EventService().getEvents(pairId).flatMap {
      case Left(error) => error
      case Right(events) =>
        QuestionService().getQuestions(pairId).flatMap {
          case Left(error)      => error
          case Right(questions) => userKeyGetLogin(userKey, userId, pairs, Option(events), Option(questions))
        }
    }
  }

  private[impl] def userKeyGetLogin(userKey: UserKey,
                                    userId: UUID,
                                    pairs: List[PairModel],
                                    events: Option[List[EventModel]],
                                    questions: Option[List[QuestionModel]])(implicit input: LoginModel,
                                                                            p: Pipeline): LoginResponse = {
    userKey.getLastLogin.flatMap { lastLogin =>
      userKey.setLastLogin().flatMap {
        case true  => LoginResponseModel(userId, pairs, events, questions, lastLogin)
        case false => Errors.userSetFailed
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

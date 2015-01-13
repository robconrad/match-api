/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 9:22 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.common.time.TimeService
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.event.EventService
import base.entity.event.model.EventModel
import base.entity.kv.Key._
import base.entity.kv.{ HashKey, KeyId, KvService }
import base.entity.logging.AuthLoggable
import base.entity.pair.PairService
import base.entity.pair.model.PairModel
import base.entity.perm.Perms
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel
import base.entity.service.CrudImplicits
import base.entity.user.LoginService
import base.entity.user.LoginService.LoginResponse
import base.entity.user.UserKeyFactories.{ DeviceKeyFactory, UserKeyFactory }
import base.entity.user.UserKeyProps._
import base.entity.user.impl.LoginServiceImpl._
import base.entity.user.model._
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class LoginServiceImpl() extends ServiceImpl with LoginService with Dispatchable with AuthLoggable {

  private implicit val ec = dispatcher

  private val crudImplicits = CrudImplicits[LoginResponseModel]
  import crudImplicits._

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
    deviceKeyGet(DeviceKeyFactory().make(KeyId(input.device.uuid)))(input, KvService().pipeline)
  }

  private[impl] def deviceKeyGet(deviceKey: HashKey)(implicit input: LoginModel,
                                                     p: Pipeline): LoginResponse = {
    deviceKey.getId(TokenProp).flatMap {
      case Some(token) if input.token == token => deviceKeySet(deviceKey)
      case Some(token)                         => externalErrorTokenNotValidResponse
      case None                                => externalErrorDeviceNotVerifiedResponse
    }
  }

  private[impl] def deviceKeySet(deviceKey: HashKey)(implicit input: LoginModel,
                                                     p: Pipeline): LoginResponse = {
    val props = Map[Prop, Any](
      AppVersionProp -> input.appVersion,
      LocaleProp -> input.locale,
      ModelProp -> input.device.model,
      CordovaProp -> input.device.cordova,
      PlatformProp -> input.device.platform,
      VersionProp -> input.device.version)
    deviceKey.set(props).flatMap {
      case true  => userKeyGet(deviceKey)
      case false => internalErrorDeviceSetFailedResponse
    }
  }

  private[impl] def userKeyGet(deviceKey: HashKey)(implicit input: LoginModel,
                                                   p: Pipeline): LoginResponse = {
    deviceKey.getId(UserIdProp).flatMap {
      case Some(userId) => getPairs(userId)
      case None         => internalErrorUserIdGetFailedResponse
    }
  }

  private[impl] def getPairs(userId: UUID)(implicit input: LoginModel,
                                           p: Pipeline): LoginResponse = {
    val userKey = UserKeyFactory().make(KeyId(userId))
    PairService().getPairs(userId).flatMap {
      case Left(error) => error
      case Right(pairs) => input.pairId match {
        case Some(pairId) => getPairEvents(userKey, userId, pairs, pairId)
        case None         => userKeyGetLogin(userKey, userId, pairs, None, None)
      }
    }
  }

  private[impl] def getPairEvents(userKey: HashKey,
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

  private[impl] def userKeyGetLogin(userKey: HashKey,
                                    userId: UUID,
                                    pairs: List[PairModel],
                                    events: Option[List[EventModel]],
                                    questions: Option[List[QuestionModel]])(implicit input: LoginModel,
                                                                            p: Pipeline): LoginResponse = {
    userKey.getDateTime(LastLoginProp).flatMap { lastLogin =>
      userKey.set(LastLoginProp, TimeService().asString()).flatMap {
        case true  => LoginResponseModel(userId, pairs, events, questions, lastLogin)
        case false => internalErrorUserSetFailedResponse
      }
    }
  }

}

object LoginServiceImpl {

  private val crudImplicits = CrudImplicits[LoginResponseModel]
  import base.entity.user.impl.LoginServiceImpl.crudImplicits._

  lazy val externalErrorDeviceNotVerified = "This device has not been verified."
  lazy val externalErrorTokenNotValid = "The supplied token is not valid."
  lazy val externalErrorLogin = "There was a problem during login."

  lazy val internalErrorDeviceSetFailed = "failed to set device attributes"
  lazy val internalErrorUserIdGetFailed = "failed to get user id from device"
  lazy val internalErrorUserSetFailed = "failed to set user attributes"

  lazy val externalErrorDeviceNotVerifiedResponse: LoginResponse =
    ApiError(externalErrorDeviceNotVerified, BadRequest, DEVICE_NOT_VERIFIED)
  lazy val externalErrorTokenNotValidResponse: LoginResponse =
    ApiError(externalErrorTokenNotValid, Unauthorized, TOKEN_NOT_VALID)
  lazy val internalErrorDeviceSetFailedResponse: LoginResponse =
    ApiError(externalErrorLogin, InternalServerError, internalErrorDeviceSetFailed)
  lazy val internalErrorUserIdGetFailedResponse: LoginResponse =
    ApiError(externalErrorLogin, InternalServerError, internalErrorUserIdGetFailed)
  lazy val internalErrorUserSetFailedResponse: LoginResponse =
    ApiError(externalErrorLogin, InternalServerError, internalErrorUserSetFailed)

}

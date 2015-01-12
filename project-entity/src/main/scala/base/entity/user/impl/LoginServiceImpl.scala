/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 7:07 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.kv.{ KvService, KeyId, HashKey }
import base.entity.kv.Key._
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.service.CrudImplicits
import base.entity.user.LoginService
import base.entity.user.LoginService.LoginResponse
import base.entity.user.UserKeyFactories.DeviceKeyFactory
import base.entity.user.UserKeyProps._
import base.entity.user.impl.LoginServiceImpl._
import base.entity.user.model._
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class LoginServiceImpl()
    extends ServiceImpl with LoginService with Dispatchable with AuthLoggable {

  private implicit val ec = dispatcher

  private val crudImplicits = CrudImplicits[LoginResponseModel]
  import crudImplicits._

  /**
   * - get device token
   * - validate provided token
   * - update device attributes
   * - retrieve pairs
   * - optionally retrieve current pair events
   * - optionally retrieve current pair questions
   * - get userId
   * - update user attributes (last login)
   */
  def login(input: LoginModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.LOGIN)
    deviceKeyGet(DeviceKeyFactory().make(KeyId(input.device.uuid)))(input, KvService().pipeline)
  }

  private[impl] def deviceKeyGet(deviceKey: HashKey)(implicit input: LoginModel,
                                                     p: Pipeline): LoginResponse = {
    deviceKey.getId(TokenProp).flatMap {
      case Some(token) => tokenValidate(deviceKey, token)
      case None        => externalErrorDeviceNotVerifiedResponse
    }
  }

  private[impl] def tokenValidate(deviceKey: HashKey, token: UUID)(implicit input: LoginModel,
                                                                   p: Pipeline): LoginResponse = {
    input.token == token match {
      case true  => deviceKeySet(deviceKey)
      case false => externalErrorTokenNotValidResponse
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
      case true  => getPairs()
      case false => internalErrorDeviceSetFailedResponse
    }
  }

  private[impl] def getPairs()(implicit input: LoginModel,
                               p: Pipeline): LoginResponse = {
    internalErrorDeviceSetFailedResponse
  }

  private[impl] def userKeySet(userKey: HashKey)(implicit input: LoginModel,
                                                 p: Pipeline): LoginResponse = {
    internalErrorDeviceSetFailedResponse
  }

}

object LoginServiceImpl {

  private val crudImplicits = CrudImplicits[LoginResponseModel]
  import base.entity.user.impl.LoginServiceImpl.crudImplicits._

  lazy val externalErrorDeviceNotVerified = "This device has not been verified."
  lazy val externalErrorTokenNotValid = "The supplied token is not valid."
  lazy val externalErrorLogin = "There was a problem during login."

  lazy val internalErrorDeviceSetFailed = "failed to set device attributes"

  lazy val externalErrorDeviceNotVerifiedResponse: LoginResponse =
    ApiError(externalErrorDeviceNotVerified, BadRequest, DEVICE_NOT_VERIFIED)
  lazy val externalErrorTokenNotValidResponse: LoginResponse =
    ApiError(externalErrorTokenNotValid, Unauthorized, TOKEN_NOT_VALID)
  lazy val internalErrorDeviceSetFailedResponse: LoginResponse =
    ApiError(externalErrorLogin, InternalServerError, internalErrorDeviceSetFailed)

}

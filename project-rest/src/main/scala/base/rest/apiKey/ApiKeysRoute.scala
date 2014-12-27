/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:16 PM
 */

package base.rest.apiKey

import base.entity.ApiStrings.Keys._
import base.entity.ApiStrings._
import base.entity.apiKey.ApiKeysService
import base.entity.apiKey.model.ApiKeys
import base.entity.auth.AuthTypeSets
import base.entity.error.ApiError
import base.entity.perm.Perms
import base.rest.Endpoint._
import base.rest.route.VersionedRestRoute
import com.wordnik.swagger.annotations._

/**
 * API route description for auth endpoint
 * @author rconrad
 */
@Api(value = Names.API_KEYS, description = endpointDesc)
private[rest] trait ApiKeysRoute extends VersionedRestRoute {

  def endpoints = List(API_KEYS)

  def restRoutes = search ~ refresh

  // format: OFF
  @ApiOperation(value = readDesc, notes = userOrSecretAuthNote, httpMethod = getMethod)
  @ApiResponses(Array(
    new ApiResponse(code = OKCode,                message = readOKDesc,           response = classOf[ApiKeys]),
    new ApiResponse(code = authErrorCode,         message = authErrorCodeDesc,    response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode,       message = serverErrorCodeDesc,  response = classOf[ApiError])))
  def search =
    path(API_KEYS) {
      get {
        auth(AuthTypeSets.USER_OR_KEY, Perms.API_KEY_READ) { implicit authCtx =>
          completeEither(ApiKeysService().get)
        }
      }
    }

  @ApiOperation(value = refreshDesc, notes = userOrSecretAuthNote, httpMethod = putMethod)
  @ApiResponses(Array(
    new ApiResponse(code = OKCode,                message = refreshOKDesc,        response = classOf[ApiKeys]),
    new ApiResponse(code = errorCode,             message = refreshErrorDesc,     response = classOf[ApiError]),
    new ApiResponse(code = authErrorCode,         message = authErrorCodeDesc,    response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode,       message = serverErrorCodeDesc,  response = classOf[ApiError])))
  def refresh =
    path(API_KEYS) {
      put {
        auth(AuthTypeSets.USER_OR_KEY, Perms.API_KEY_REFRESH) { implicit authCtx =>
          completeEither(ApiKeysService().refresh)
        }
      }
    }
  // format: ON

}

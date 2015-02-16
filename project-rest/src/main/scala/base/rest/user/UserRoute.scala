/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.rest.user

import base.entity.api.ApiStrings
import base.entity.api.ApiStrings.User._
import base.entity.api.ApiStrings._
import base.entity.error.model.ApiError
import base.entity.user.model.UserModel
import base.rest.Endpoint._
import base.rest.route.{ RestVersionsRoute, VersionedRestRoute }
import com.wordnik.swagger.annotations._

// scalastyle:off line.size.limit
@Api(value = Names.USERS, description = endpointDesc)
private[rest] trait UserRoute extends VersionedRestRoute {

  def endpoints = List(USERS)

  def restRoutes = describe ~ create

  // format: OFF
  def describe =
    path(USERS) {
      pathEndOrSingleSlash {
        get {
          completeResponse(Map(RestVersionsRoute.ENDPOINTS -> s"${USERS.toString(version)}/{id}"))
        }
      }
    }

  @ApiOperation(value = createDesc, notes = userOrSecretOrPublishableAuthNote, httpMethod = postMethod)
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = email,            dataType = stringDataType,      paramType = formParamType, value = emailDesc,     required = true),
    new ApiImplicitParam(name = password,         dataType = stringDataType,      paramType = formParamType, value = passwordDesc,  required = true)))
  @ApiResponses(Array(
    new ApiResponse(code = OKCode,                message = createOKDesc,         response = classOf[UserModel]),
    new ApiResponse(code = errorCode,             message = createErrorDesc,      response = classOf[ApiError]),
    new ApiResponse(code = authErrorCode,         message = authErrorCodeDesc,    response = classOf[ApiError]),
    new ApiResponse(code = mediaErrorCode,        message = mediaErrorCodeDesc,   response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode,       message = serverErrorCodeDesc,  response = classOf[ApiError])))
  def create =
    path(USERS) {
      post {
//        auth(AuthTypeSets.USER_OR_KEY, Perms.USER_CREATE) { authCtx =>
//          entity(as[RegisterModel]) { input =>
//            completeEither(UserService().create(authCtx, input))
//          }
//        }
        completeNotImplemented
      }
    }
  // format: ON

}

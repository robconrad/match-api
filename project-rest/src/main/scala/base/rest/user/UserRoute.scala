/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 7:16 PM
 */

package base.rest.user

import javax.ws.rs.Path

import base.entity.ApiStrings.User._
import base.entity.ApiStrings._
import base.entity.auth.AuthTypeSets
import base.entity.auth.context.{ AuthContext, UserAuthContext }
import base.entity.error.ApiError
import base.entity.perm.Perms
import base.entity.user.model.{ PostResetRequest, PostUserRequest, PutUserRequest, UserModel }
import base.entity.user.UserService
import base.rest.Endpoint._
import base.rest.route.{ RestVersionsRoute, VersionedRestRoute }
import com.wordnik.swagger.annotations._
import scala.reflect.runtime.universe.typeTag

// scalastyle:off line.size.limit
@Api(value = Names.USERS, description = endpointDesc)
private[rest] trait UserRoute extends VersionedRestRoute {

  def endpoints = List(USERS)

  def restRoutes = describe ~ create ~ update ~ read ~ readMe ~ resetInitiate ~ resetComplete

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
        auth(AuthTypeSets.USER_OR_KEY, Perms.USER_CREATE) { authCtx =>
          entity(as[PostUserRequest]) { input =>
            completeEither(UserService().create(authCtx, input))
          }
        }
      }
    }

  @ApiOperation(value = updateDesc, notes = notYetImplementedNote, httpMethod = putMethod)
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = id,               dataType = uuidDataType,        paramType = pathParamType, value = idDesc,        required = true),
    new ApiImplicitParam(name = email,            dataType = stringDataType,      paramType = formParamType, value = emailDesc),
    new ApiImplicitParam(name = password,         dataType = stringDataType,      paramType = formParamType, value = passwordDesc),
    new ApiImplicitParam(name = active,           dataType = stringDataType,      paramType = formParamType, value = activeDesc)))
  @ApiResponses(Array(
    new ApiResponse(code = OKCode,                message = updateOKDesc,         response = classOf[UserModel]),
    new ApiResponse(code = errorCode,             message = updateErrorDesc,      response = classOf[ApiError]),
    new ApiResponse(code = authErrorCode,         message = authErrorCodeDesc,    response = classOf[ApiError]),
    new ApiResponse(code = notFoundCode,          message = updateNotFoundDesc,   response = classOf[ApiError]),
    new ApiResponse(code = mediaErrorCode,        message = mediaErrorCodeDesc,   response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode,       message = serverErrorCodeDesc,  response = classOf[ApiError])))
  def update =
    path(USERS / JavaUUID) { userId =>
      put {
        auth(AuthTypeSets.USER_OR_KEY, Perms.USER_UPDATE) { authCtx =>
          entity(as[PutUserRequest]) { input =>
            completeEither(UserService().update(authCtx, userId, input))
          }
        }
      } ~
      corsOptions
    }

  @ApiOperation(value = readDesc, notes = notYetImplementedNote, httpMethod = getMethod)
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = id,               dataType = uuidDataType,        paramType = pathParamType, value = idDesc,        required = true)))
  @ApiResponses(Array(
    new ApiResponse(code = OKCode,                message = readOKDesc,           response = classOf[UserModel]),
    new ApiResponse(code = authErrorCode,         message = authErrorCodeDesc,    response = classOf[ApiError]),
    new ApiResponse(code = notFoundCode,          message = readNotFoundDesc,     response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode,       message = serverErrorCodeDesc,  response = classOf[ApiError])))
  def read =
    path(USERS / JavaUUID) { userId =>
      get {
        auth(AuthTypeSets.USER_OR_KEY, Perms.USER_READ) { authCtx =>
          completeEither(UserService().get(authCtx, userId))
        }
      } ~
      corsOptions
    }

  @Path(Names.PATH_USERS_ME)
  @ApiOperation(value = readMeDesc, notes = userAuthNote, httpMethod = getMethod)
  @ApiResponses(Array(
    new ApiResponse(code = OKCode, message = readOKDesc, response = classOf[UserModel]),
    new ApiResponse(code = authErrorCode, message = authErrorCodeDesc, response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode, message = serverErrorCodeDesc, response = classOf[ApiError])))
  def readMe =
    path(USERS_ME) {
      get {
        auth(AuthTypeSets.USER, Perms.USER_READ_ME) { implicit authCtx =>
          completeResponse(UserModel(authCtx.userThrows))
        }
      } ~
      corsOptions
    }

  @Path(Names.PATH_USERS_RESET)
  @ApiOperation(value = resetInitDesc, notes = notYetImplementedNote, httpMethod = postMethod)
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = email,            dataType = stringDataType,      paramType = formParamType, value = emailDesc)))
  @ApiResponses(Array(
    new ApiResponse(code = OKCode,                message = resetInitOKDesc),
    new ApiResponse(code = errorCode,             message = resetInitErrorDesc,   response = classOf[ApiError]),
    new ApiResponse(code = notFoundCode,          message = resetInitNotFoundDesc, response = classOf[ApiError]),
    new ApiResponse(code = mediaErrorCode,        message = mediaErrorCodeDesc,   response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode,       message = serverErrorCodeDesc,  response = classOf[ApiError])))
  def resetInitiate =
    path(USERS_RESET) {
      post {
        entity(as[PostResetRequest]) { input =>
          completeEither(UserService().resetInitiate(input))
        }
      } ~
      corsOptions
    }

  @Path(Names.PATH_USERS_RESET)
  @ApiOperation(value = resetCompleteDesc, notes = notYetImplementedNote, httpMethod = getMethod)
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = resetCode,        dataType = stringDataType,      paramType = queryParamType, value = resetCodeDesc)))
  @ApiResponses(Array(
    new ApiResponse(code = foundCode,             message = resetCompleteFoundDesc),
    new ApiResponse(code = errorCode,             message = resetCompleteErrorDesc, response = classOf[ApiError]),
    new ApiResponse(code = notFoundCode,          message = resetCompleteNotFoundDesc, response = classOf[ApiError]),
    new ApiResponse(code = serverErrorCode,       message = serverErrorCodeDesc,  response = classOf[ApiError])))
  def resetComplete =
    path(USERS_RESET) {
      get {
        parameter('c.as[String]) { resetCode =>
          //redirect(Uri(##user service foo##, StatusCodes.Found)
          completeEither(UserService().resetComplete(resetCode))
        }
      }
    }
  // format: ON

}

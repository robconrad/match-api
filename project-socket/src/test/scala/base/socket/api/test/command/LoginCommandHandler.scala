/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/7/15 5:17 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.common.service.TestServices
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.{ApiErrorCodes, ApiVersions}
import base.entity.auth.context.ChannelContext
import base.entity.device.model.DeviceModel
import base.entity.error.ApiErrorService
import base.entity.error.model.impl.ApiErrorImpl
import base.entity.facebook.FacebookInfo
import base.entity.facebook.impl.FacebookServiceImpl
import base.entity.user.model.impl.LoginResponseModelImpl
import base.entity.user.model.{ LoginModel, LoginResponseModel }
import base.socket.api._
import base.socket.api.test.util.ListUtils._
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{ SocketConnection, TestGroup }
import base.socket.command.impl.CommandProcessingServiceImpl.Errors
import spray.http.StatusCodes

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class LoginCommandHandler(implicit s: SocketConnection) extends CommandHandler {

  def apply(group: Option[TestGroup] = None)(implicit executor: CommandExecutor,
                                             questions: TestQuestions, randomMock: RandomServiceMock) {

    if (s.userIdOpt.isEmpty) {
      s.userId = randomMock.nextUuid()
    }

    lazy val deviceModel = DeviceModel(s.deviceId)
    lazy val loginModel = LoginModel(s.facebookToken, group.map(_.id), "", ApiVersions.V01, "", deviceModel)
    lazy val loginResponseModel: LoginResponseModel = LoginResponseModelImpl(
      s.userModel,
      s.phoneOpt,
      s.phoneOpt.isDefined,
      List(),
      sortGroups(s.groups.map(_.model)),
      group.map(_.events.reverse),
      group.map(group => questions.filteredModels(group.id, s.questionsAnswered(group.id))),
      s.lastLogin)
    lazy val errorResponseModel = ApiErrorService().errorCodeSeed(Errors.externalErrorText,
      StatusCodes.BadRequest, ApiErrorCodes.ALREADY_LOGGED_IN, "this socket is already authenticated")
    lazy val fbInfo = FacebookInfo(s.facebookToken, s.name, "male", "EN_us")

    val unregister = registerFacebookService(Option(fbInfo))

    s.isLoggedIn match {
      case true => executor(loginModel, Option(errorResponseModel))
      case false => executor(loginModel, Option(loginResponseModel))
    }

    unregister()

    s.lastLogin = TimeServiceConstantMock.now
    s.isLoggedIn = true
  }

  def registerFacebookService(fbInfo: Option[FacebookInfo]) = {
    val service = new FacebookServiceImpl(1.day) {
      override def getInfo(token: String)(implicit channelCtx: ChannelContext) = Future.successful(fbInfo)
    }
    TestServices.register(service)
  }

}

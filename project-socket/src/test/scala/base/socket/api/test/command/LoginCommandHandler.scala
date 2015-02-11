/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 5:03 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.common.service.TestServices
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.ChannelContext
import base.entity.device.model.DeviceModel
import base.entity.facebook.FacebookInfo
import base.entity.facebook.impl.FacebookServiceImpl
import base.entity.user.model.impl.LoginResponseModelImpl
import base.entity.user.model.{ LoginModel, LoginResponseModel }
import base.socket.api._
import base.socket.api.test.util.ListUtils._
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{ SocketConnection, TestGroup }

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

    val deviceModel = DeviceModel(s.deviceId)
    val loginModel = LoginModel(s.facebookToken, group.map(_.id), "", ApiVersions.V01, "", deviceModel)
    val loginResponseModel: LoginResponseModel = LoginResponseModelImpl(
      s.userModel,
      s.phoneOpt,
      s.phoneOpt.isDefined,
      List(),
      sortGroups(s.groups.map(_.model)),
      group.map(_.events.reverse),
      group.map(group => questions.filteredModels(group.id, s.questionsAnswered(group.id))),
      s.lastLogin)
    val fbInfo = FacebookInfo(s.facebookToken, s.name, "male", "EN_us")

    val unregister = registerFacebookService(Option(fbInfo))

    executor(loginModel, Option(loginResponseModel))

    unregister()

    s.lastLogin = TimeServiceConstantMock.now
  }

  def registerFacebookService(fbInfo: Option[FacebookInfo]) = {
    val service = new FacebookServiceImpl(1.day) {
      override def getInfo(token: String)(implicit channelCtx: ChannelContext) = Future.successful(fbInfo)
    }
    TestServices.register(service)
  }

}

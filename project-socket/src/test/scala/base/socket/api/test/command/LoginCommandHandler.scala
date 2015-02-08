/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 3:55 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.common.service.TestServices
import base.entity.api.ApiVersions
import base.entity.auth.context.ChannelContext
import base.entity.device.model.DeviceModel
import base.entity.event.model.EventModel
import base.entity.facebook.FacebookInfo
import base.entity.facebook.impl.FacebookServiceImpl
import base.entity.group.model.GroupModel
import base.entity.user.model.impl.LoginResponseModelImpl
import base.entity.user.model.{LoginModel, LoginResponseModel}
import base.socket.api.test.SocketConnection
import base.socket.api.test.util.ListUtils._
import base.socket.api.test.util.TestQuestions
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class LoginCommandHandler(implicit s: SocketConnection) extends CommandHandler {

  def apply(groups: List[GroupModel], groupId: Option[UUID], phone: Option[String],
            events: Option[List[EventModel]] = None, filteredQuestions: Option[List[Int]] = None,
            lastLogin: Option[DateTime] = None)(implicit executor: CommandExecutor, questions: TestQuestions) {
    val deviceModel = DeviceModel(s.deviceId)
    val loginModel = LoginModel(s.facebookToken, groupId, "", ApiVersions.V01, "", deviceModel)
    val loginResponseModel: LoginResponseModel = LoginResponseModelImpl(
      s.userModel, phone, phone.isDefined,
      List(), sortGroups(groups), events, filteredQuestions.map(questions.filteredModels), lastLogin)
    val fbInfo = FacebookInfo(s.facebookToken, s.name, "male", "EN_us")
    val unregister = registerFacebookService(Option(fbInfo))
    executor(loginModel, Option(loginResponseModel))
    unregister()
  }

  def registerFacebookService(fbInfo: Option[FacebookInfo]) = {
    val service = new FacebookServiceImpl(1.day) {
      override def getInfo(token: String)(implicit channelCtx: ChannelContext) = Future.successful(fbInfo)
    }
    TestServices.register(service)
  }

}

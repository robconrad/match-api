/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 11:41 AM
 */

package base.socket.api.test.command

import java.util.UUID

import base.common.service.TestServices
import base.entity.api.ApiVersions
import base.entity.auth.context.ChannelContext
import base.entity.device.model.DeviceModel
import base.entity.event.model.EventModel
import base.entity.facebook.impl.FacebookServiceImpl
import base.entity.facebook.{FacebookInfo, FacebookService}
import base.entity.group.model.GroupModel
import base.entity.question.model.QuestionModel
import base.entity.user.model.impl.LoginResponseModelImpl
import base.entity.user.model.{LoginModel, LoginResponseModel, UserModel}
import base.socket.api.test.ListUtils._
import base.socket.api.test.SocketConnection
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class LoginCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  import socket.props._

  def apply(groups: List[GroupModel], groupId: Option[UUID], phone: Option[String],
            events: Option[List[EventModel]] = None, questions: Option[List[QuestionModel]] = None,
            lastLogin: Option[DateTime] = None)(implicit executor: CommandExecutor) {
    val deviceModel = DeviceModel(deviceId)
    val loginModel = LoginModel(facebookToken, groupId, "", ApiVersions.V01, "", deviceModel)
    val loginResponseModel: LoginResponseModel = LoginResponseModelImpl(
      UserModel(userId, Option(FacebookService().getPictureUrl(facebookToken)), Option(name)), phone, phone.isDefined,
      List(), sortGroups(groups), events, questions.map(sortQuestions), lastLogin)
    val fbInfo = FacebookInfo(facebookToken, name, "male", "EN_us")
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

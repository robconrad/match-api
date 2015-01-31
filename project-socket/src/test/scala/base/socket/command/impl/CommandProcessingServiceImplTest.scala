/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:44 AM
 */

package base.socket.command.impl

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.model.CommandModel
import base.entity.json.JsonFormats
import base.entity.user.model._
import base.entity.user.{ RegisterPhoneCommandService, VerifyPhoneCommandService }
import base.socket.command.CommandProcessingService.CommandProcessResult
import base.socket.service.SocketServiceTest
import org.json4s.native.Serialization

import scala.concurrent.Future

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class CommandProcessingServiceImplTest extends SocketServiceTest {

  val service = new CommandProcessingServiceImpl()

  private val phone = "555-5555"

  implicit val formats = JsonFormats.withModels
  implicit val channelCtx = ChannelContextDataFactory.userAuth

  def testCommand[A, B <: AnyRef](model: A, response: B)(implicit channelCtx: ChannelContext,
                                                         mA: Manifest[A], mB: Manifest[B]) {
    val cmd = CommandModel(model)
    val input = Serialization.write(cmd)

    val responseJson = Serialization.write(response)
    val expected = CommandProcessResult(Option(responseJson), None)

    assert(service.process(input).await() == Right(expected))
  }

  ignore("anything else") {
    fail("not tested")
  }

  test("command - register") {
    val model = RegisterPhoneModel(phone)
    val response = RegisterPhoneResponseModel(phone)
    val command = CommandModel(response)
    val service = mock[RegisterPhoneCommandService]
    (service.execute(_: RegisterPhoneModel)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Option(command))
    val unregister = TestServices.register(service)
    testCommand(model, command)
    unregister()
  }

  test("command - verify") {
    val model = VerifyPhoneModel(phone, "code")
    val response = VerifyPhoneResponseModel(RandomService().uuid.toString)
    val command = CommandModel(response)
    val service = mock[VerifyPhoneCommandService]
    (service.execute(_: VerifyPhoneModel)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Option(command))
    val unregister = TestServices.register(service)
    testCommand(model, command)
    unregister()
  }

}

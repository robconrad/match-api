/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 2:55 PM
 */

package base.socket.command.impl

import base.common.random.RandomService
import base.common.service.TestServices
import base.common.test.TestExceptions.TestRuntimeException
import base.entity.api.ApiErrorCodes
import base.entity.api.ApiErrorCodes.ErrorCode
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.model.CommandModel
import base.entity.json.JsonFormats
import base.entity.user.model._
import base.entity.user.{ RegisterPhoneCommandService, VerifyPhoneCommandService }
import base.socket.command.CommandProcessingService._
import base.socket.command.impl.CommandProcessingServiceImpl.Errors
import base.socket.service.SocketServiceTest
import org.json4s._
import org.json4s.native.Serialization
import spray.http.StatusCodes

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

  def assertError(input: String, errorCode: ErrorCode): Unit = {
    service.process(input).await() match {
      case Left(error)   => assert(error.message.code.contains(errorCode))
      case Right(result) => fail(result.toString)
    }
  }

  def assertExtractThrows(throws: () => FutureResponse) = {
    val command = new service.ProcessCommand() {
      override def extractCommand(json: JValue) = {
        throws()
      }
    }
    val error = command.parseInput("").await() match {
      case Left(error)   => error.message
      case Right(result) => fail(result.toString)
    }
    assert(error.message == Errors.externalErrorText)
    assert(error.status == StatusCodes.InternalServerError)
  }

  ignore("anything else") {
    fail("not tested")
  }

  test("json errors") {
    assertError("", ApiErrorCodes.JSON_NOT_FOUND)
    assertError("{}", ApiErrorCodes.JSON_COMMAND_NOT_FOUND)
    assertError("{\"cmd\":\"foo\"}", ApiErrorCodes.JSON_COMMAND_NOT_FOUND)
    assertError("{\"cmd\":\"login\"}", ApiErrorCodes.JSON_BODY_NOT_FOUND)

  }

  test("parsing throws") {
    assertExtractThrows(() => throw new TestRuntimeException("oh noes"))
    assertExtractThrows(() => Future.failed(new TestRuntimeException("oh noes")))
  }

  test("failed to serialize") {
    val command = new service.ProcessCommand() {
      override def serializeResult(msg: Any) = None
    }
    command.returnResult("", None) match {
      case Right(result) => fail(result.toString)
      case Left(error)   => assert(error.message.debug.exists(_.contains("failed to serialize message")))
    }
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
    val response = VerifyPhoneResponseModel(RandomService().uuid.toString, List())
    val command = CommandModel(response)
    val service = mock[VerifyPhoneCommandService]
    (service.execute(_: VerifyPhoneModel)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Option(command))
    val unregister = TestServices.register(service)
    testCommand(model, command)
    unregister()
  }

}

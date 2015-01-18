/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 4:13 PM
 */

package base.socket.api

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.Socket

import base.common.lib.Genders
import base.common.logging.Loggable
import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, ServicesBeforeAndAfterAll }
import base.common.test.Tags
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.command.CommandServiceCompanion
import base.entity.command.model.CommandModel
import base.entity.device.model.DeviceModel
import base.entity.group.model.{ GroupModel, InviteModel, InviteResponseModel }
import base.entity.json.JsonFormats
import base.entity.kv.KvTest
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.impl.VerifyCommandServiceImpl
import base.entity.user.model._
import base.entity.user.{ LoginCommandService, RegisterCommandService, VerifyCommandService }
import base.socket.test.SocketBaseSuite
import org.json4s.native.Serialization

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class SocketApiIntegrationTest extends SocketBaseSuite with ServicesBeforeAndAfterAll with Loggable with KvTest {

  private implicit val formats = JsonFormats.withEnumsAndFields

  private val randomMock = new RandomServiceMock()
  private val time = TimeServiceConstantMock.now

  override def beforeAll() {
    super.beforeAll()

    // full integration except sms.. don't want to have external side effects
    Services.register(new SmsServiceMock(result = true))

    // use real verify service but swap code validation to be always true
    val codeLength = 6
    Services.register(new VerifyCommandServiceImpl(codeLength, "whatever") {
      override def validateVerifyCodes(code1: String, code2: String) = true
    })

    Services.register(randomMock)
    Services.register(TimeServiceConstantMock)

    assert(SocketApiService().start().await())
  }

  override def afterAll() {
    super.beforeAll()
    assert(SocketApiService().stop().await())
  }

  private def execute[A, B](companion: CommandServiceCompanion[_],
                            model: A, responseModel: B)(implicit out: PrintWriter, in: BufferedReader) {

    val command = CommandModel(companion.inCmd, model)
    val json = Serialization.write(command)

    val responseCommand = CommandModel(companion.outCmd, responseModel)
    val responseJson = Serialization.write(responseCommand)

    out.write(json + "\r\n")
    out.flush()

    assert(in.readLine() == responseJson)
  }

  test("integration test - runs all commands", Tags.SLOW) {
    val socket = new Socket(SocketApiService().host, SocketApiService().port)
    socket.setSoTimeout(timeout.duration.toMillis.toInt)

    implicit val out = new PrintWriter(socket.getOutputStream, true)
    implicit val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

    val phone = "555-1234"
    val deviceId = RandomService().uuid
    val userId = randomMock.nextUuid()

    val registerModel = RegisterModel(ApiVersions.V01, phone)
    val registerResponseModel = RegisterResponseModel()
    execute(RegisterCommandService, registerModel, registerResponseModel)

    val code = "code!"
    val token = randomMock.nextUuid()

    val verifyModel = VerifyModel(ApiVersions.V01, Option("name"), Option(Genders.other), phone, deviceId, code)
    val verifyResponseModel = VerifyResponseModel(token)
    execute(VerifyCommandService, verifyModel, verifyResponseModel)

    val deviceModel = DeviceModel(deviceId, "", "", "", "", "")
    val loginModel = LoginModel(token, None, "", ApiVersions.V01, "", deviceModel)
    val loginResponseModel = LoginResponseModel(userId, List(), None, None, None)
    execute(LoginCommandService, loginModel, loginResponseModel)

    val inviteUserId = randomMock.nextUuid()
    val groupId = randomMock.nextUuid(1)

    val groupModel = GroupModel(groupId, List(), time, time, 0)
    val inviteModel = InviteModel("555-5432", "bob")
    val inviteResponseModel = InviteResponseModel(inviteUserId, groupModel)
    // TODO enable once GroupServices are implemented
    //execute(InviteCommandService, inviteModel, inviteResponseModel)
  }

}

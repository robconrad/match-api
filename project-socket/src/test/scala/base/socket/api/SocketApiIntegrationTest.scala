/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:28 AM
 */

package base.socket.api

import base.common.test.Tags
import base.entity.api.ApiErrorCodes
import base.entity.error.ApiErrorService
import base.socket.api.test.IntegrationSuite
import base.socket.command.impl.CommandProcessingServiceImpl
import spray.http.StatusCodes

/**
 * {{ Describe the high level purpose of SocketApiIntegrationTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SocketApiIntegrationTest extends IntegrationSuite {

  test("integration test - runs all commands", Tags.SLOW) {
    val (socket1, socket2, socket3) = (makeSocket(), makeSocket(), makeSocket())
    val (group1, group2, group3) = (makeGroup(), makeGroup(), makeGroup())

    // first user logs in, does some stuff by himself including creating a group
    socket1.connect()
    socket1.login()
    socket1.login()
    socket1.register()
    socket1.verify()
    socket1.sendInvite(group1, socket2)
    socket1.createQuestion(group1)
    socket1.questions(group1)
    socket1.message(group1)

    // second user comes in response to invite from first, accepts invite, sends a message and answers a question
    socket2.connect()
    socket2.login()
    socket2.register()
    socket2.verify()
    socket2.acceptInvite(group1)
    socket2.ackEvents(group1)

    // first user answer down here because we couldn't match until second user exists
    socket1.answer(group1, socket2)
    socket1.ackEvents(group1)
    socket2.createQuestion(group1)
    socket2.questions(group1)
    socket2.message(group1)

    // exhaust the available standard questions so we start answering user generated questions
    for (i <- 0 until questions.standardModels.size - socket2.questionsAnswered(group1.id).size)
      socket2.answer(group1, socket1)

    // third user comes along and gets invited to a new group by first user *after* account is
    //  created by login, and declines the invite
    socket3.connect()
    socket3.login()
    socket1.sendInvite(group2, socket3)
    socket3.register()
    socket3.verify()
    socket3.declineInvite(group2)

    // third user goes on to invite second user who accepts but then leaves before the message is sent
    socket3.sendInvite(group3, socket2)
    socket2.acceptInvite(group3)
    socket3.disconnect()
    socket2.message(group3)
    socket2.ackEvents(group3)

    // everybody else leaves
    socket1.disconnect()
    socket2.disconnect()

    // do it all again (minus the registrations and invites)
    socket1.connect()
    socket2.connect()
    socket3.connect()
    socket1.login(Option(group2))
    socket2.login(Option(group3))
    socket3.login(Option(group3))
    socket1.createQuestion(group1)
    socket1.questions(group1)
    socket1.message(group1)
    socket1.answer(group1, socket2)
    socket2.createQuestion(group1)
    socket2.questions(group1)
    socket2.message(group1)
    socket2.answer(group1, socket1)
    socket2.message(group3)

    // everybody leaves
    socket1.disconnect()
    socket2.disconnect()
    socket3.disconnect()
  }

  test("error", Tags.SLOW) {
    implicit val socket = makeSocket().connect()

    assert(socket.isActive)
    socket.write("")
    executor.assertResponse(ApiErrorService().errorCodeSeed(
      CommandProcessingServiceImpl.Errors.externalErrorText,
      StatusCodes.BadRequest,
      ApiErrorCodes.JSON_NOT_FOUND,
      "no json received in msg: JNothing"))
    // not disconnecting on known errors at the moment
    //assert(!socket.isActive)

    socket.disconnect()
  }

}

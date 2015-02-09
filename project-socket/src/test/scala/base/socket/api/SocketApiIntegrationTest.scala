/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:56 PM
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
    val (socket1, socket1a, socket2, socket3) = (makeSocket(), makeSocket(), makeSocket(), makeSocket())
    val (group1, group2, group3) = (makeGroup(), makeGroup(), makeGroup())

    // first user logs in, does some stuff by himself including creating a group
    socket1.connect()
    socket1.login()
    socket1.register()
    socket1.verify()
    socket1.sendInvite(group1, socket2)
    socket1.questions(group1)
    socket1.message(group1)

    // second user comes in response to invite from first, accepts invite, sends a message and answers a question
    socket2.connect()
    socket2.login()
    socket2.register()
    socket2.verify()
    socket2.acceptInvite(group1)
    // first user answer down here because we couldn't match until second user exists
    socket1.answer(group1, socket2)
    socket2.questions(group1)
    socket2.message(group1)
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

    // everybody else leaves
    socket1.disconnect()
    socket2.disconnect()

    // first user repeat login tests all his info is there (e.g. lastLogin, etc.) and specifies a default group
    socket1a.props = socket1.props
    socket1a.connect()
    socket1a.login(Option(group1))
    socket1a.disconnect()
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
    assert(!socket.isActive)

    socket.disconnect()
  }

}

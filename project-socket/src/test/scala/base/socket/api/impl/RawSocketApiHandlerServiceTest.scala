/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:40 PM
 */

package base.socket.api.impl

import java.io.IOException
import java.nio.channels.ClosedChannelException
import javax.net.ssl.SSLException

import base.common.test.TestExceptions.TestRuntimeException
import base.entity.auth.context.NoAuthContext
import base.socket.api.mock.{ ChannelMock, ChannelHandlerContextMock }
import base.socket.service.SocketServiceTest

/**
 * {{ Describe the high level purpose of SocketApiHandlerServiceTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class RawSocketApiHandlerServiceTest extends SocketServiceTest {

  val service = new RawSocketApiHandlerServiceImpl()

  def testExceptionCaught(t: Throwable, closeCalled: Boolean = false) {
    val channel = new ChannelMock(isOpen = true)
    val ctx = new ChannelHandlerContextMock(channel)
    service.exceptionCaught(ctx, t)
    assert(channel.getCloseCalled == closeCalled)
  }

  test("exceptionCaught") {
    testExceptionCaught(new ClosedChannelException())
    testExceptionCaught(new IOException())
    testExceptionCaught(new SSLException(""))
    testExceptionCaught(new IllegalArgumentException())
    testExceptionCaught(new TestRuntimeException(""), closeCalled = true)
  }

  ignore("channelRead") {
    fail("not tested")
  }

  ignore("channelActive") {
    fail("not tested")
  }

}

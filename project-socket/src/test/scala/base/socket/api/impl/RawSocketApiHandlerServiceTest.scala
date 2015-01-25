/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:19 AM
 */

package base.socket.api.impl

import java.io.IOException
import java.nio.channels.ClosedChannelException
import javax.net.ssl.SSLException

import base.common.service.TestServices
import base.common.test.TestExceptions.TestRuntimeException
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.group.GroupListenerService
import base.socket.api.mock.{ ChannelHandlerContextMock, ChannelMock }
import base.socket.service.SocketServiceTest
import io.netty.channel.{ Channel, ChannelFuture, ChannelHandlerContext }
import io.netty.util.Attribute

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
    channel.channelCtx = ChannelContextDataFactory.emptyUserAuth
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

  // todo I mean really wtf
  ignore("channelActive") {
    // this all doesn't really test the point - want to ensure groupListener.unregister is called
    //  but we can't tell that because the closeFuture ends up getting mocked out. Needs to be refactored.
    val groupListener = mock[GroupListenerService]
    val unregister = TestServices.register(groupListener)
    val ctx = mock[ChannelHandlerContext]
    val channel = mock[Channel]
    val attribute = mock[Attribute[ChannelContext]]
    val closeFuture = mock[ChannelFuture]
    ctx.channel _ expects () returning channel
    channel.attr[ChannelContext] _ expects * returning attribute
    attribute.set _ expects *
    ctx.channel _ expects () returning channel
    channel.closeFuture _ expects () returning closeFuture
    closeFuture.addListener _ expects * returning closeFuture
    service.channelActive(ctx)
    channel.close().awaitUninterruptibly()
    unregister()
  }

}

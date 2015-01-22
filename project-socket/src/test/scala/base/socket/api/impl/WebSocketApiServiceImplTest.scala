/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:14 PM
 */

package base.socket.api.impl

import base.socket.api.mock.{ WebSocketApiHandlerServiceMock, RawSocketApiHandlerServiceMock }
import base.socket.api.test.WebSocketClientFactory
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import org.scalatest.concurrent.Eventually

/**
 * {{ Describe the high level purpose of RawSocketApiServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class WebSocketApiServiceImplTest extends SocketApiServiceImplTest with Eventually {

  def makeMock = new WebSocketApiHandlerServiceMock(channelReadResponse = Option(response))

  def assertResponse() {
    import base.socket.api.test._

    val ch = WebSocketClientFactory.connect()

    ch.write(new TextWebSocketFrame(response.toString))
    ch.flush()

    eventually {
      assert(ch.lastMessage.isDefined)
    }

    assert(ch.lastMessage.get == response)
  }

}

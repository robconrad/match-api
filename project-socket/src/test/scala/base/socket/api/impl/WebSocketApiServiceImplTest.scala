/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:58 PM
 */

package base.socket.api.impl

import base.socket.api.mock.SocketApiHandlerServiceMock

/**
 * {{ Describe the high level purpose of RawSocketApiServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class WebSocketApiServiceImplTest extends SocketApiServiceImplTest {

  def makeMock = new SocketApiHandlerServiceMock(channelReadResponse = Option(response),
    handler => new WebSocketChannelInitializer(handler))

  def assertResponse() {
    fail("not implemented")
  }

}

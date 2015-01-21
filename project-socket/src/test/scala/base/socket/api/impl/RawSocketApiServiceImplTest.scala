/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:56 PM
 */

package base.socket.api.impl

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.Socket

import base.socket.api.mock.SocketApiHandlerServiceMock

/**
 * {{ Describe the high level purpose of RawSocketApiServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class RawSocketApiServiceImplTest extends SocketApiServiceImplTest {

  def makeMock = new SocketApiHandlerServiceMock(channelReadResponse = Option(response),
    handler => new RawSocketChannelInitializer(handler))

  def assertResponse() {
    val socket = new Socket(host, port)
    socket.setSoTimeout(defaultTimeout.duration.toMillis.toInt)

    val out = new PrintWriter(socket.getOutputStream, true)
    val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

    out.write(response.toString + "\r\n")
    out.flush()

    assert(in.readLine() == response)
  }

}

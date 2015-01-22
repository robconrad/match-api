/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:00 PM
 */

package base.socket.api

import base.socket.api.impl.WebSocketApiHandlerServiceImpl
import base.socket.api.test._
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx._
import org.scalatest.concurrent.Eventually

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
// scalastyle:off
class WebSocketApiIntegrationTest extends SocketApiIntegrationTest with Eventually {

  override implicit val patienceConfig = PatienceConfig(defaultSpan)

  private var ch: Channel = _

  def handlerService = new WebSocketApiHandlerServiceImpl

  def connect() {
    ch = WebSocketClientFactory.connect()
  }

  def disconnect() {
    ch.close
  }

  def writeRead(json: String) = {
    ch.write(new TextWebSocketFrame(json))
    ch.flush()

    eventually {
      assert(ch.lastMessage.isDefined)
    }
    val response = ch.lastMessage.get

    ch.lastMessage = None
    response
  }

}

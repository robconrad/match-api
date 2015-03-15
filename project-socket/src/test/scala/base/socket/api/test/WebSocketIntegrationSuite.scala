/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:31 AM
 */

package base.socket.api.test

import base.socket.api.impl.WebSocketApiHandlerServiceImpl
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx._
import org.scalatest.concurrent.Eventually

import scala.language.existentials

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
// scalastyle:off
private[api] trait WebSocketIntegrationSuite extends IntegrationSuite with Eventually {

  override implicit val patienceConfig = PatienceConfig(defaultSpan)

  def handlerService = new WebSocketApiHandlerServiceImpl

  def makeSocket(props: SocketProperties) =
    new SocketConnection(props) {

      private var ch: Channel = _

      protected def _connect() = {
        ch = WebSocketClientFactory.connect()
        this
      }

      protected def _disconnect() = {
        ch.close
        this
      }

      def read = {
        eventually {
          assert(ch.lastMessage.isDefined)
        }
        val response = ch.lastMessage.get

        ch.lastMessage = None
        response
      }

      def write(json: String) {
        ch.write(new TextWebSocketFrame(json))
        ch.flush()
      }

      def isActive = ch.isActive

    }

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:13 PM
 */

package base.socket.api.impl

import java.net.InetSocketAddress

import base.common.service.ServiceImpl
import base.socket.handler.AuthenticationHandler
import base.socket.netty.{ JsonDecoder, JsonEncoder }
import base.socket.api.SocketApiService
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ ChannelHandlerContext, ChannelInitializer, ChannelOption }
import io.netty.handler.codec.{ DelimiterBasedFrameDecoder, Delimiters }
import io.netty.handler.timeout.{ IdleStateEvent, IdleStateHandler }
import io.netty.util.concurrent.GenericFutureListener

import scala.concurrent.duration._
import scala.concurrent.{ Future, Promise }
import scala.util.{ Failure, Success }

/**
 * {{ Describe the high level purpose of SocketServerServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SocketApiServiceImpl(val host: String,
                           val port: Int,
                           shutdownTime: FiniteDuration) extends ServiceImpl with SocketApiService {

  private lazy val eventLoop1 = new NioEventLoopGroup
  private lazy val eventLoop2 = new NioEventLoopGroup
  private lazy val eventLoops = Set(eventLoop1, eventLoop2)

  def start() = {
    val p = Promise[Boolean]()

    val port = 8888
    val address = new InetSocketAddress(port)

    Future {
      val server = new ServerBootstrap
      server.group(eventLoop1, eventLoop2)
        .localAddress(address)
        .channel(classOf[NioServerSocketChannel])
        .childOption[java.lang.Boolean](ChannelOption.TCP_NODELAY, true)
        .childOption[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)
        .childOption[java.lang.Boolean](ChannelOption.SO_REUSEADDR, true)
        .childOption[java.lang.Integer](ChannelOption.SO_LINGER, 0)
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) {
            val pipeline = ch.pipeline
            val maxFrameLength = 8192
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(maxFrameLength, Delimiters.lineDelimiter: _*))

            pipeline.addLast("decoder", JsonDecoder)
            pipeline.addLast("encoder", JsonEncoder)

            val readWriteIdleTime = 0
            val allIdleTime = 60
            pipeline.addLast("timeout", new IdleStateHandler(readWriteIdleTime, readWriteIdleTime, allIdleTime) {
              override def channelIdle(ctx: ChannelHandlerContext, evt: IdleStateEvent) {
                ctx.close()
              }
            })

            pipeline.addLast("authHandler", AuthenticationHandler)
          }
        })
      server.bind().syncUninterruptibly()
      info("Listening on %s:%s", address.getAddress.getHostAddress, address.getPort)
    } onComplete {
      case Success(v) =>
        p success true
      case Failure(f) =>
        error(s"Unable to bind to %s. Check your configuration.", f, address)
        p completeWith stop()
    }

    p.future
  }

  def stop() = {
    // stop processing new input
    AuthenticationHandler.stop()

    // allow time for in process messages to wind downs
    Thread.sleep(shutdownTime.toMillis)

    // Shut down all event loops to terminate all threads.
    val f = eventLoops.map(_.shutdownGracefully()).map { f =>
      val p = Promise[Boolean]()
      f.addListener(new GenericFutureListener[Nothing] {
        def operationComplete(future: Nothing) {
          p success true
        }
      })
      p.future
    }

    Future.sequence(f).map(set => !set.contains(false))
  }

}

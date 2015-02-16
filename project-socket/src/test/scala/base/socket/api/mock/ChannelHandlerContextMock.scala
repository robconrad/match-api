/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.socket.api.mock

import java.net.SocketAddress

import io.netty.channel.{ Channel, ChannelHandlerContext, ChannelPromise }
import io.netty.util.AttributeKey

/**
 * {{ Describe the high level purpose of ChannelHandlerContextMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
class ChannelHandlerContextMock(val channel: Channel) extends ChannelHandlerContext {

  def fireChannelUnregistered() = null

  def fireChannelInactive() = null

  def fireChannelActive() = null

  def fireChannelRegistered() = null

  def fireChannelReadComplete() = null

  def handler() = null

  def executor() = null

  def flush() = null

  def name() = null

  def fireChannelWritabilityChanged() = null

  def fireUserEventTriggered(event: scala.Any) = null

  def fireExceptionCaught(cause: Throwable) = null

  def isRemoved = false

  def fireChannelRead(msg: scala.Any) = null

  def voidPromise() = null

  def newProgressivePromise() = null

  def alloc() = null

  def newFailedFuture(cause: Throwable) = null

  def newPromise() = null

  def pipeline() = null

  def newSucceededFuture() = null

  def attr[T](key: AttributeKey[T]) = null

  def writeAndFlush(msg: scala.Any, promise: ChannelPromise) = null

  def writeAndFlush(msg: scala.Any) = null

  def disconnect() = null

  def disconnect(promise: ChannelPromise) = null

  def write(msg: scala.Any) = null

  def write(msg: scala.Any, promise: ChannelPromise) = null

  def close() = null

  def close(promise: ChannelPromise) = null

  def deregister() = null

  def deregister(promise: ChannelPromise) = null

  def read() = null

  def connect(remoteAddress: SocketAddress) = null

  def connect(remoteAddress: SocketAddress, localAddress: SocketAddress) = null

  def connect(remoteAddress: SocketAddress, promise: ChannelPromise) = null

  def connect(remoteAddress: SocketAddress, localAddress: SocketAddress, promise: ChannelPromise) = null

  def bind(localAddress: SocketAddress) = null

  def bind(localAddress: SocketAddress, promise: ChannelPromise) = null

}

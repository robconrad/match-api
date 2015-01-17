/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 1:53 PM
 */

package base.socket.api.mock

import java.net.SocketAddress

import io.netty.channel.local.LocalAddress
import io.netty.channel.{ ChannelPromise, Channel }
import io.netty.util.{ DefaultAttributeMap, Attribute, AttributeKey }

/**
 * {{ Describe the high level purpose of ChannelMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
class ChannelMock(val isOpen: Boolean = true) extends Channel {

  private val attributes = new DefaultAttributeMap()

  private var closeCalled = false

  def eventLoop() = null

  def isRegistered = true

  def unsafe() = null

  def config() = null

  def metadata() = null

  def closeFuture() = null

  def remoteAddress() = new LocalAddress("id")

  def isActive = true

  def flush() = null

  def localAddress() = null

  def isWritable = true

  def read() = null

  def parent() = null

  def compareTo(o: Channel) = 0

  def attr[T](key: AttributeKey[T]) = attributes.attr(key)

  def writeAndFlush(msg: scala.Any, promise: ChannelPromise) = null

  def writeAndFlush(msg: scala.Any) = null

  def disconnect() = null

  def disconnect(promise: ChannelPromise) = null

  def write(msg: scala.Any) = null

  def write(msg: scala.Any, promise: ChannelPromise) = null

  def close() = {
    closeCalled = true
    null
  }

  def close(promise: ChannelPromise) = {
    closeCalled = true
    null
  }

  def getCloseCalled = closeCalled

  def deregister() = null

  def deregister(promise: ChannelPromise) = null

  def connect(remoteAddress: SocketAddress) = null

  def connect(remoteAddress: SocketAddress, localAddress: SocketAddress) = null

  def connect(remoteAddress: SocketAddress, promise: ChannelPromise) = null

  def connect(remoteAddress: SocketAddress, localAddress: SocketAddress, promise: ChannelPromise) = null

  def bind(localAddress: SocketAddress) = null

  def bind(localAddress: SocketAddress, promise: ChannelPromise) = null

  def voidPromise() = null

  def newProgressivePromise() = null

  def alloc() = null

  def newFailedFuture(cause: Throwable) = null

  def newPromise() = null

  def pipeline() = null

  def newSucceededFuture() = null

}

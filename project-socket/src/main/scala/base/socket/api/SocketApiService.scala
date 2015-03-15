/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 9:59 AM
 */

package base.socket.api

import base.common.service.{ ApiService, ServiceCompanion }

import scala.concurrent.duration.FiniteDuration

/**
 * Responsible for configuring, starting, binding, etc. the Socket API
 * @author rconrad
 */
trait SocketApiService extends ApiService {

  final val serviceManifest = manifest[SocketApiService]

  val name = "socket-api"

  /**
   * Socket Server Connection Information
   */
  def host: String
  def port: Int

  def idleTimeout: FiniteDuration

  def isConnectionAllowed: Boolean

}

object SocketApiService extends ServiceCompanion[SocketApiService]

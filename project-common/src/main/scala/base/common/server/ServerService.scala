/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.server

import akka.actor.ActorSystem
import akka.util.Timeout
import base.common.service.{ Service, ServiceCompanion }

import scala.concurrent.duration.Duration

/**
 * Server-level configuration and operations
 * @author rconrad
 */
trait ServerService extends Service {

  final def serviceManifest = manifest[ServerService]

  /**
   * Default timeout for futures, actors, etc. across the system
   */
  def defaultDuration: Duration
  def defaultTimeout: Timeout

  /**
   * Force some Akka config because it's a little bitch that doesn't listen well
   */
  def makeActorSystem(): ActorSystem

}

object ServerService extends ServiceCompanion[ServerService]

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 1:55 PM
 */

package base.common.service

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.duration.Duration

/**
 * Server-level configuration and operations
 * @author rconrad
 */
trait CommonService extends Service {

  final def serviceManifest = manifest[CommonService]

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

object CommonService extends ServiceCompanion[CommonService]

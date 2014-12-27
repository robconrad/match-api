/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.lib

import java.net.SocketTimeoutException

import akka.actor.{ SupervisorStrategy, SupervisorStrategyConfigurator }
import base.common.lib.Exceptions.{ RestartActorRuntimeException, ResumeActorRuntimeException, StopActorRuntimeException }
import base.common.logging.Loggable

/**
 * Implements Akka supervisor strategies
 * @author rconrad
 */
private[lib] class ActorSupervisor extends SupervisorStrategyConfigurator with Loggable {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._

  import scala.concurrent.duration._

  private val MAX_RETRIES = 6

  def resume(e: Throwable) = {
    warn("Resuming Actor because of throwable: " + Loggable.stackTraceToString(e))
    Resume
  }

  def restart(e: Throwable) = {
    error("Restarting Actor because of throwable: " + Loggable.stackTraceToString(e))
    Restart
  }

  def stop(e: Throwable) = {
    error("Stopping Actor because of throwable: " + Loggable.stackTraceToString(e))
    Stop
  }

  def create(): SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = MAX_RETRIES, withinTimeRange = 1.minute) {
      // stoppable exceptions
      case e: StopActorRuntimeException    => stop(e)

      // resumable exceptions
      case e: ResumeActorRuntimeException  => resume(e)

      // restartable exceptions
      case e: RestartActorRuntimeException => restart(e)
      case e: SocketTimeoutException       => restart(e)
      case e: IllegalArgumentException     => restart(e)
      case e: NullPointerException         => restart(e)

      // catchall
      case e                               => restart(e)
    }
}

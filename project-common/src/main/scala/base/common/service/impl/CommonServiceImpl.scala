/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 3:43 PM
 */

package base.common.service.impl

import akka.actor.ActorSystem
import akka.util.Timeout
import base.common.lib.BaseConfig
import base.common.service.{ CommonService, ServiceImpl }
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

/**
 * Server-level configuration and operations
 * @param akkaHost hostname Akka will
 * @param akkaPort port Akka will bind to
 * @param defaultDuration default timeout for futures, actors, etc. across the system
 * @author rconrad
 */
private[common] class CommonServiceImpl(akkaHost: String,
                                        akkaPort: Int,
                                        val defaultDuration: FiniteDuration) extends ServiceImpl with CommonService {

  implicit val defaultTimeout = new Timeout(defaultDuration)

  /**
   * Cycle up through ports to find one unoccupied if default is already bound
   *  (akka is shitty about letting go of ports in test and development situations)
   */
  def makeActorSystem(): ActorSystem = {
    var system: Option[ActorSystem] = None
    var port = akkaPort
    while (system == None && port < akkaPort + 100) {
      try {
        system = Option(bindActorSystem(port))
      } catch {
        case e: Throwable => port += 1
      }
    }
    system.getOrElse(throw new ExceptionInInitializerError("no actor system created"))
  }

  /**
   * Force some Akka config because it doesn't listen well
   */
  private def bindActorSystem(port: Int): ActorSystem = {
    val conf = ConfigFactory.parseString(
      """
        | akka {
        |   actor {
        |     guardian-supervisor-strategy = "base.common.lib.ActorSupervisor"
        |     provider = "akka.remote.RemoteActorRefProvider"
        |     default-dispatcher {
        |       type = "Dispatcher"
        |       executor = "fork-join-executor"
        |       mailbox-type = "akka.dispatch.UnboundedDequeBasedMailbox"
        |       fork-join-executor {
        |         parallelism-min = 32
        |         parallelism-factor = 3.0
        |         parallelism-max = 128
        |       }
        |     }
        |   }
        |   remote.netty.tcp {
        |     hostname = "%s"
        |     port = %s
        |   }
        | }
      """.stripMargin.format(akkaHost, port))
    ActorSystem("base", conf.withFallback(BaseConfig.tsConf))
  }

}

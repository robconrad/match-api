/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:33 PM
 */

package base.rest.server

import base.common.lib.Dispatchable
import base.common.logging.Loggable
import base.common.service.CommonServicesBootstrap
import base.entity.db.EvolutionService
import base.entity.service.EntityServicesBootstrap
import base.rest.service.RestServicesBootstrap

import scala.util.Failure

/**
 * App Server!
 * @author rconrad
 */
private[rest] object Server extends App with Loggable with Dispatchable { PS =>

  val serviceName = "base-api"

  start()

  /**
   * Setup the API, create hooks, etc.
   */
  def start() {
    info(s"Starting $serviceName")

    // initialize base.common services (e.g. akka)
    CommonServicesBootstrap.registered

    // initialize database services
    //  (must be before all other service types since most depend on DbService being present)
    EntityServicesBootstrap.dbRegistered

    // once we are finished getting the database up-to-current, register all remaining services and setup the API
    EvolutionService().evolve().onComplete {
      case Failure(e) => throw e
      case _ =>
        // business logic layer services are first
        EntityServicesBootstrap.registered

        // finally the rest services come online to respond to requests
        RestServicesBootstrap.registered

        // now that all services are initialized, bring up the REST API and bind it
        RestServerService().start().onFailure { case e => throw e }
    }

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() {
        PS.stop()
      }
    })
  }

  /**
   * Shut down the API - any cleanup on shutdown goes here
   */
  def stop() {
    info(s"Stopping $serviceName")
  }

}

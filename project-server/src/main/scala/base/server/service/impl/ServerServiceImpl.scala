/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:13 PM
 */

package base.server.service.impl

import base.common.service.{ CommonServicesBootstrap, ServiceImpl }
import base.entity.db.EvolutionService
import base.entity.service.EntityServicesBootstrap
import base.rest.api.RestApiService
import base.rest.service.RestServicesBootstrap
import base.server.service.ServerService
import base.socket.api.SocketApiService
import base.socket.service.SocketServicesBootstrap

import scala.concurrent.Future

/**
 * Responsible for configuring, starting, binding, etc. the APIs
 * @author rconrad
 */
class ServerServiceImpl(kv: Boolean,
                        db: Boolean,
                        rest: Boolean,
                        socket: Boolean) extends ServiceImpl with ServerService {

  private val bootstraps = Map(
    RestApiService -> RestServicesBootstrap,
    SocketApiService -> SocketServicesBootstrap)

  private lazy val services = bootstraps.keys.filter {
    case RestApiService   => rest
    case SocketApiService => socket
  }.toSet

  def start() = {
    info("Starting APIs")

    // initialize base.common services (e.g. akka)
    CommonServicesBootstrap.registered

    // initialize database services
    //  (must be before all other service types since many may depend on DbService or KvService being present)
    if (db) {
      EntityServicesBootstrap.dbRegistered
    }
    if (kv) {
      EntityServicesBootstrap.kvRegistered
    }

    // once we are finished getting the database up-to-current, register all remaining services and setup the API
    val evolutionFuture = db match {
      case true  => EvolutionService().evolve()
      case false => Future.successful()
    }

    // once the db is evolved (if necessary), boot up business logic service then start APIs
    evolutionFuture.flatMap { u =>

      // business logic layer services are first
      EntityServicesBootstrap.otherRegistered

      val results = services.map { service =>
        // register services for the given API
        bootstraps(service).registered

        info("Starting API %s", service().name)

        // start the given API
        service().start().map { result =>
          info("Started API %s with result %s", service().name, result)
          result
        }
      }

      Future.sequence(results).map { results =>
        !results.contains(false)
      }
    }
  }

  def stop() = {
    info("Stopping APIs")

    val results = services.map { service =>
      info("Stopping API %s", service().name)
      service().stop().map { result =>
        info("Stopped API %s with result %s", service().name, result)
        result
      }
    }

    Future.sequence(results).map { results =>
      !results.contains(false)
    }
  }

}

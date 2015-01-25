/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:23 AM
 */

package base.rest.route

import akka.actor.Actor
import base.common.logging.Loggable
import base.entity.json.JsonFormats
import base.rest.RequestResponseRecorder
import org.json4s.Formats
import spray.http.HttpHeaders.{ `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin` }
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.Json4sSupport
import spray.routing.HttpService
import spray.routing.directives.LoggingMagnet

/**
 * Instances of this actor will serve all incoming requests to the server
 *  (i.e. it will have the complete route including all versions and base types)
 * @author rconrad
 */
private[rest] class RoutingActor extends Actor with HttpService with Json4sSupport with RoutingHandlers with Loggable {

  implicit val json4sFormats: Formats = JsonFormats.withModels

  def actorRefFactory = context

  def receive = runRoute {
    logRequestResponse {
      LoggingMagnet(recordRequestResponseValues)
    } {
      MasterRouteFactory(context)
    }
  }

  /**
   * Spray compatible log entry
   */
  private def recordRequestResponseValues(request: HttpRequest)(response: Any) {
    RequestResponseRecorder(request, response)
  }

}

private[rest] object RoutingActor {

  /**
   * CORS headers block non-whitelisted clients (browsers) from communicating with the server.
   *  - Right now we allow all origins but in the future we may want to whitelist to just the domains
   *     of our ISOs/merchants.
   *  - We allow essentially all methods since the REST server operates on all of these (except OPTIONS
   *     which is really just for CORS only)
   *  - The list of allowed headers is everything the client should be sending. Will have to include
   *     our custom auth headers when they are implemented
   */
  val corsHeaders = List(
    `Access-Control-Allow-Origin`(AllOrigins),
    `Access-Control-Allow-Methods`(GET, POST, PUT, OPTIONS, DELETE),
    `Access-Control-Allow-Headers`(
      "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, " +
        "Accept-Language, Host, Referer, User-Agent, WWW-Authenticate, Authorization"))

}

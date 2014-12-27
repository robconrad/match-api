/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:53 AM
 */

package base.rest.swagger

import akka.actor.ActorRefFactory
import base.rest.VersionedEndpoint
import base.rest.Versions.Version
import base.rest.route.{ MasterVersionedRouteFactory, RouteFactory }

/**
 * Builds spray routes for the Swagger endpoints that describe the rest of the API as JSON
 * @author rconrad
 */
private[rest] object SwaggerRestRouteFactory extends RouteFactory {

  def buildRoute(actors: ActorRefFactory, currentVersion: Version) = new SwaggerRestRoute {
    def version = currentVersion
    def actorRefFactory = actors
    def apiTypes = MasterVersionedRouteFactory.routeFactories.map(_.getTypes(currentVersion)).flatten.toSeq
    def baseUrl = VersionedEndpoint(currentVersion, None, "")
  }.routes

  // swagger endpoints are not themselves included in API docs
  def getTypes(version: Version) = Set()

}

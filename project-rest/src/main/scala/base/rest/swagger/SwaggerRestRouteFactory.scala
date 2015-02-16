/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.rest.swagger

import akka.actor.ActorRefFactory
import base.entity.api.ApiVersions
import base.entity.api.ApiVersions.ApiVersion
import base.rest.VersionedEndpoint
import base.rest.route.{ MasterVersionedRouteFactory, RouteFactory }

/**
 * Builds spray routes for the Swagger endpoints that describe the rest of the API as JSON
 * @author rconrad
 */
private[rest] object SwaggerRestRouteFactory extends RouteFactory {

  def buildRoute(actors: ActorRefFactory, currentVersion: ApiVersion) = new SwaggerRestRoute {
    def version = currentVersion
    def actorRefFactory = actors
    def apiTypes = MasterVersionedRouteFactory.routeFactories.map(_.getTypes(currentVersion)).flatten.toSeq
    def baseUrl = VersionedEndpoint(currentVersion, None, "")
  }.routes

  // swagger endpoints are not themselves included in API docs
  def getTypes(version: ApiVersion) = Set()

}

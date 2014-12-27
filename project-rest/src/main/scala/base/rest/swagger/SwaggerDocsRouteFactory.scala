/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:45 AM
 */

package base.rest.swagger

import akka.actor.ActorRefFactory
import base.rest.Versions.Version
import base.rest.route.RouteFactory

/**
 * Builds spray routes for the Swagger documentation pages (i.e. the HTML outside the API)
 * @author rconrad
 */
private[rest] object SwaggerDocsRouteFactory extends RouteFactory {

  def buildRoute(actors: ActorRefFactory, currentVersion: Version) = new SwaggerDocsRoute {
    def version = currentVersion
    def actorRefFactory = actors
  }.routes

  // swagger endpoints are not themselves included in API docs
  def getTypes(version: Version) = Set()

}

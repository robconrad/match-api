/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:10 PM
 */

package base.rest.swagger

import akka.actor.ActorRefFactory
import base.entity.api.ApiVersions
import base.entity.api.ApiVersions.ApiVersion
import base.rest.route.RouteFactory

/**
 * Builds spray routes for the Swagger documentation pages (i.e. the HTML outside the API)
 * @author rconrad
 */
private[rest] object SwaggerDocsRouteFactory extends RouteFactory {

  def buildRoute(actors: ActorRefFactory, currentVersion: ApiVersion) = new SwaggerDocsRoute {
    def version = currentVersion
    def actorRefFactory = actors
  }.routes

  // swagger endpoints are not themselves included in API docs
  def getTypes(version: ApiVersion) = Set()

}

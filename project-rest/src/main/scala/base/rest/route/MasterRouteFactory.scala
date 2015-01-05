/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:45 PM
 */

package base.rest.route

import akka.actor.ActorRefFactory
import base.entity.api.ApiVersions
import base.rest.swagger.SwaggerVersionsRouteFactory
import spray.routing.RouteConcatenation

/**
 * Registrar of all routes connected to the main spray routing actor
 * @author rconrad
 */
private[rest] object MasterRouteFactory extends RouteConcatenation {

  def apply(actors: ActorRefFactory) = {
    val versionedRoutes = MasterVersionedRouteFactory(actors, ApiVersions.values.toSeq: _*)
    val swaggerVersions = SwaggerVersionsRouteFactory(actors)
    val versionsRoute = RestVersionsRouteFactory(actors)
    val staticRoute = StaticRouteFactory(actors)

    versionedRoutes ~ swaggerVersions ~ versionsRoute ~ staticRoute
  }

}

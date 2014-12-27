/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:43 AM
 */

package base.rest.route

import akka.actor.ActorRefFactory
import base.rest.Versions
import base.rest.swagger.SwaggerVersionsRouteFactory
import spray.routing.RouteConcatenation

/**
 * Registrar of all routes connected to the main spray routing actor
 * @author rconrad
 */
private[rest] object MasterRouteFactory extends RouteConcatenation {

  def apply(actors: ActorRefFactory) = {
    val versionedRoutes = MasterVersionedRouteFactory(actors, Versions.values.toSeq: _*)
    val swaggerVersions = SwaggerVersionsRouteFactory(actors)
    val versionsRoute = RestVersionsRouteFactory(actors)
    val staticRoute = StaticRouteFactory(actors)

    versionedRoutes ~ swaggerVersions ~ versionsRoute ~ staticRoute
  }

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:53 AM
 */

package base.rest.route

import akka.actor.ActorRefFactory
import base.rest.Versions.Version
import base.rest.apiKey.ApiKeysRouteFactory
import base.rest.auth.AuthRouteFactory
import base.rest.swagger.{ SwaggerDocsRouteFactory, SwaggerRestRouteFactory }
import base.rest.user.UserRouteFactory
import spray.routing.{ Route, RouteConcatenation }

/**
 * Producer of complete routes for a given version or set of versions of the API
 *  NB: all RouteFactories must be registered here for Swagger to pick them up
 * @author rconrad
 */
private[rest] object MasterVersionedRouteFactory extends RouteConcatenation {

  // registry of RouteFactories, used by this factory to produce the final routes and
  //  by Swagger to get the reflections added to each
  val routeFactories = Set[RouteFactory](
    AuthRouteFactory,
    ApiKeysRouteFactory,
    UserRouteFactory,
    SwaggerDocsRouteFactory,
    SwaggerRestRouteFactory
  )

  /**
   * Create a master route including subroutes for all specified versions
   */
  def apply(actors: ActorRefFactory, versions: Version*): Route = {
    versions.map(apply(actors, _)).reduce((a, b) => a ~ b)
  }

  /**
   * Create a master route for the specified version
   */
  def apply(actors: ActorRefFactory, version: Version): Route = {
    routeFactories.map(_.buildRoute(actors, version)).reduce((a, b) => a ~ b)
  }

}

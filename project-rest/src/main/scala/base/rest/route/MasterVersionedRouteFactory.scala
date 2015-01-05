/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:45 PM
 */

package base.rest.route

import akka.actor.ActorRefFactory
import base.entity.api.ApiVersions
import ApiVersions.ApiVersion
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
  def apply(actors: ActorRefFactory, versions: ApiVersion*): Route = {
    versions.map(apply(actors, _)).reduce((a, b) => a ~ b)
  }

  /**
   * Create a master route for the specified version
   */
  def apply(actors: ActorRefFactory, version: ApiVersion): Route = {
    routeFactories.map(_.buildRoute(actors, version)).reduce((a, b) => a ~ b)
  }

}

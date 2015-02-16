/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:10 PM
 */

package base.rest.route

import akka.actor.ActorRefFactory
import base.entity.api.ApiVersions
import base.entity.api.ApiVersions._
import spray.routing.Route

import scala.reflect.runtime.universe.Type

/**
 * Base class for objects responsible for creating routes
 * @author rconrad
 */
private[rest] trait RouteFactory {

  /**
   * Create the complete route for which this factory is responsible for the specified API version
   */
  def buildRoute(actors: ActorRefFactory, version: ApiVersion): Route

  /**
   * Contains reflected type data for the route(s) served by the specified API version
   */
  def getTypes(version: ApiVersion): Set[Type]

}

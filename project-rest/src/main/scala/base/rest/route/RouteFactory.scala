/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.route

import akka.actor.ActorRefFactory
import base.rest.Versions._
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
  def buildRoute(actors: ActorRefFactory, version: Version): Route

  /**
   * Contains reflected type data for the route(s) served by the specified API version
   */
  def getTypes(version: Version): Set[Type]

}

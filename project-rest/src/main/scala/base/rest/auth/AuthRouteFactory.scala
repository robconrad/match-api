/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.auth

import akka.actor.ActorRefFactory
import base.rest.Versions._
import base.rest.route.RouteFactory

import scala.reflect.runtime.universe._

/**
 * Builds spray routes for the Auth endpoint
 * @author rconrad
 */
private[rest] object AuthRouteFactory extends RouteFactory {

  val types = Set(typeOf[AuthRoute])

  def buildRoute(actors: ActorRefFactory, currentVersion: Version) = new AuthRoute {
    def version = currentVersion
    def actorRefFactory = actors
  }.routes

  def getTypes(version: Version) = types

}

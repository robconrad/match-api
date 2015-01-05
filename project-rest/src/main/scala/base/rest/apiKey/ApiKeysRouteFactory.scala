/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:45 PM
 */

package base.rest.apiKey

import akka.actor.ActorRefFactory
import base.entity.api.ApiVersions
import ApiVersions._
import base.rest.route.RouteFactory

import scala.reflect.runtime.universe._

/**
 * Builds spray routes for the Keys endpoint
 * @author rconrad
 */
private[rest] object ApiKeysRouteFactory extends RouteFactory {

  val types = Set(typeOf[ApiKeysRoute])

  def buildRoute(actors: ActorRefFactory, currentVersion: ApiVersion) = new ApiKeysRoute {
    def version = currentVersion
    def actorRefFactory = actors
  }.routes

  def getTypes(version: ApiVersion) = types

}

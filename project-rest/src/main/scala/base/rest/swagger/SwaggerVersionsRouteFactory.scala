/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:40 AM
 */

package base.rest.swagger

import akka.actor.ActorRefFactory

/**
 * Builds spray routes for the Swagger page that provides navigation to the
 *  various doc roots for each API version
 *  (explicitly not a RouteFactory since it is the only route that is not versioned)
 * @author rconrad
 */
private[rest] object SwaggerVersionsRouteFactory {

  def apply(actors: ActorRefFactory) = new SwaggerVersionsRoute {
    def actorRefFactory = actors
  }.routes

}

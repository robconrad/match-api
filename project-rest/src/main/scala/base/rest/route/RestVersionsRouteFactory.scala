/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.route

import akka.actor.ActorRefFactory

/**
 * Builds the main endpoint discoverability routes
 * @author rconrad
 */
private[rest] object RestVersionsRouteFactory {

  def apply(actors: ActorRefFactory) = new RestVersionsRoute {
    def actorRefFactory = actors
  }.routes

}

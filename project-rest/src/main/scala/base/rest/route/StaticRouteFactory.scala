/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.route

import akka.actor.ActorRefFactory

/**
 * Builds the stupid hackery route for IE
 * @author rconrad
 */
private[rest] object StaticRouteFactory {

  def apply(actors: ActorRefFactory) = new StaticRoute {
    def actorRefFactory = actors
  }.routes

}

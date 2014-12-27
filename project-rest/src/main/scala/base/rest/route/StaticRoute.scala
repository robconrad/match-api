/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.route

import base.rest.Locations._
import shapeless.HNil
import spray.routing._

/**
 * Messed up shit. Static content used to hack around IE shortcomings in cross-domain
 *  communication. Exposes static html from resources on the api domain.
 * @author rconrad
 */
private[rest] trait StaticRoute extends BaseRoute {

  /**
   * Directive requiring the first path segment be /static/
   */
  def pathBaseTop = new Directive0 {
    def happly(f: HNil => Route): Route =
      pathPrefix(STATIC).happly(f)
  }

  def routes =
    pathBaseTop {
      getFromResourceDirectory("static")
    }

}

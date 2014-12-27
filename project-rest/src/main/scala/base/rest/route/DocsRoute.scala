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
 * Base class for all Docs routes, restricts routing to /rest-docs/ which is outside the /rest/ API
 * @author rconrad
 */
private[rest] trait DocsRoute extends BaseRoute {

  /**
   * Directive requiring the first path segment be /rest-docs/
   */
  def pathBaseTop = new Directive0 {
    def happly(f: HNil => Route): Route =
      pathPrefix(REST_DOCS).happly(f)
  }

}

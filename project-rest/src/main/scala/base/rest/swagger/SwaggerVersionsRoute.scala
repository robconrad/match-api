/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:53 AM
 */

package base.rest.swagger

import base.rest.route.DocsRoute
import spray.http.{ StatusCodes, Uri }

/**
 * Builds a route for the Swagger page that provides navigation to the
 *  various doc roots for each API version
 * @author rconrad
 */
private[rest] trait SwaggerVersionsRoute extends DocsRoute {

  // format: OFF
  def routes =
    pathBaseTop {
      pathEnd { ctx =>
        ctx.redirect(Uri(ctx.request.uri + "/"), StatusCodes.MovedPermanently)
      } ~
      pathSingleSlash {
        getFromResource("swagger/versions.html")
      } ~
      getFromResourceDirectory("swagger")
    }
  // format: ON

}

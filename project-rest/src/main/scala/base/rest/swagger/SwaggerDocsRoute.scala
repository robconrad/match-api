/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:53 AM
 */

package base.rest.swagger

import base.rest.Versions.Version
import base.rest.route.DocsRoute
import spray.http.{ StatusCodes, Uri }

/**
 * Provides the swagger SPA to the /rest-docs/$version/ endpoint
 * @author rconrad
 */
private[rest] trait SwaggerDocsRoute extends DocsRoute {

  def version: Version

  // format: OFF
  def routes =
    pathBase(version) {
      pathEnd { ctx =>
        ctx.redirect(Uri(ctx.request.uri + "/"), StatusCodes.MovedPermanently)
      } ~
      pathSingleSlash {
        getFromResource("swagger/index.html")
      } ~
      getFromResourceDirectory("swagger")
    }
  // format: ON

}

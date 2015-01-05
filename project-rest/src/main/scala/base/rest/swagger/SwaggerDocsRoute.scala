/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:45 PM
 */

package base.rest.swagger

import base.entity.api.ApiVersions
import ApiVersions.ApiVersion
import base.rest.route.DocsRoute
import spray.http.{ StatusCodes, Uri }

/**
 * Provides the swagger SPA to the /rest-docs/$version/ endpoint
 * @author rconrad
 */
private[rest] trait SwaggerDocsRoute extends DocsRoute {

  def version: ApiVersion

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

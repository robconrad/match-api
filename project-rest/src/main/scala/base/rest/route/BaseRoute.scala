/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:33 PM
 */

package base.rest.route

import base.common.lib.Dispatchable
import base.common.logging.Loggable
import base.entity.json.JsonFormats
import base.rest.Locations._
import base.rest.Versions._
import org.json4s.Formats
import shapeless.HNil
import spray.httpx.Json4sSupport
import spray.routing._

/**
 * Base class defining behavior base.common to all routes in our API.
 *  Don't put it here unless it makes sense for REST and DOC routes (and anything else that might show up)
 * @author rconrad
 */
private[rest] trait BaseRoute extends HttpService with Json4sSupport with Dispatchable with Loggable {

  // no special formats, override below to include special fields
  implicit val json4sFormats: Formats = JsonFormats.default

  // allow implicit conversion of api location types throughout all routes
  implicit def versionToPathMatcher(restVersion: Version): PathMatcher0 = restVersion.toString
  implicit def locationToPathMatcher(restLocation: Location): PathMatcher0 = restLocation.toString

  /**
   * The primary characteristic of a route class is that it defines a route.
   */
  def routes: Route

  /**
   * Typically the first tier of base classes below this will provide a directive requiring
   *  the first path segment
   */
  def pathBaseTop: Directive0

  /**
   * Given an API version produce a directive that requires that version be the next segment
   *  of the path
   */
  def pathBase(version: Version) = new Directive0 {
    def happly(f: HNil => Route): Route =
      pathBaseTop {
        pathPrefix(version).happly(f)
      }
  }

}

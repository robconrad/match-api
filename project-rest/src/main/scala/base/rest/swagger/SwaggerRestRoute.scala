/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 10:55 AM
 */

package base.rest.swagger

import base.rest.Locations
import base.rest.route.VersionedRestRoute
import com.gettyimages.spray.swagger.SwaggerApiBuilder
import com.wordnik.swagger.config.SwaggerConfig
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiInfo

import scala.reflect.runtime.universe.Type

/**
 * Builds a rest hierarchy mirroring the actual API hierarchy
 *  with all the metadata included in the annotations of the API routes, etc.
 * @author rconrad
 */
private[rest] trait SwaggerRestRoute extends VersionedRestRoute {

  // this is a special route that handles corsOptions itself and provides no endpoints list
  def endpoints = List()

  def apiTypes: Seq[Type]
  def swaggerVersion = SwaggerSpec.version
  def baseUrl: String
  def apiInfo: Option[ApiInfo] = None

  private val api =
    new SwaggerApiBuilder(
      new SwaggerConfig(
        version,
        swaggerVersion,
        baseUrl,
        "", //api path, not used
        List(), //authorizations
        apiInfo), apiTypes)

  // format: OFF
  def restRoutes =
    pathBase(version) {
      path(Locations.DOCS) {
        completeResponse(api.getResourceListing())
      } ~
      {
        for {
          (subPath, apiListing) <- api.listings
        }
        yield {
          path(Locations.DOCS.toString / subPath.drop(1).split('/').map(segmentStringToPathMatcher).reduceLeft(_ / _)) {
            completeResponse(apiListing)
          }
        }
      }.reduceLeft(_ ~ _)
    }
  // format: ON

}

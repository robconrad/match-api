/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:45 PM
 */

package base.rest.route

import base.entity.api.ApiVersions
import base.rest.{ Locations, VersionedEndpoint }
import org.json4s.DefaultFormats
import spray.httpx.Json4sSupport
import spray.httpx.marshalling.ToResponseMarshallable

/**
 * Discoverability route for the main rest and version endpoints. Allows users to call GET on
 *  /, /rest, /rest/1.0, etc. and get back a list of child endpoints
 * @author rconrad
 */
private[rest] trait RestVersionsRoute extends RestRoute {

  // this is a special route that handles corsOptions itself and provides no endpoints list
  def endpoints = List()

  def restRoutes = naked ~ versions ~ endpointRoutes

  // format: OFF
  val naked = pathEndOrSingleSlash {
    get {
      completeResponse(RestVersionsRoute.nakedResponseMarshallable)
    } ~
    corsOptions
  }

  val versions = pathBaseTop {
    pathEndOrSingleSlash {
      get {
        completeResponse(RestVersionsRoute.versionsResponseMarshallable)
      } ~
      corsOptions
    }
  }

  val endpointRoutes = ApiVersions.available.map { version =>
    pathBase(version) {
      pathEndOrSingleSlash {
        get {
          completeResponse(RestVersionsRoute.endpointsResponsesMarshallable(version))
        } ~
        corsOptions
      }
    }
  }.reduce(_ ~ _)
  // format: ON

}

private[rest] object RestVersionsRoute extends Json4sSupport {

  implicit def json4sFormats = DefaultFormats

  val ENDPOINTS = "endpoints"
  val VERSIONS = "versions"

  /**
   * Pre-marshalled responses
   */

  // response to the bare / endpoint
  val nakedResponse = Map(
    ENDPOINTS -> List(
      "/" + Locations.REST,
      "/" + Locations.REST_DOCS))
  val nakedResponseMarshallable: ToResponseMarshallable = nakedResponse

  // response to the /rest endpoint                \
  val versionsResponse = Map(
    VERSIONS -> ApiVersions.available.map(_.toString).toList,
    ENDPOINTS -> VersionedEndpoint.restAvailable.map(_.toString))
  val versionsResponseMarshallable: ToResponseMarshallable = versionsResponse

  // responses to the /rest/$version endpoints
  val endpointsResponses = VersionedEndpoint.available.map {
    case (version, available) =>
      version -> Map(ENDPOINTS -> available.map(_.toString).toList)
  }
  val endpointsResponsesMarshallable = endpointsResponses.map {
    case (version, response) =>
      val marshallable: ToResponseMarshallable = response
      version -> marshallable
  }

}

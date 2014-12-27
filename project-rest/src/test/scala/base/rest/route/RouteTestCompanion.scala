/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.route

/**
 * Required companion for all RouteTest implementations defines what endpoints should be tested for CORS comaptibility
 * @author rconrad
 */
private[rest] trait RouteTestCompanion {

  val CORS_ENDPOINTS: List[String]

  def assertEndpointsLength() {
    assert(CORS_ENDPOINTS.length > 0)
  }

}

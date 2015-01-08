/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/7/15 10:34 PM
 */

package base.rest.route

import base.rest.user.UserRouteTest

/**
 * Integration test for all routes, hits cors endpoints for.. everything
 * @author rconrad
 */
class MasterRouteFactoryTest extends RouteTest(MasterRouteFactoryTest) {

  def pathBaseTop = get

  def routes = MasterRouteFactory(system)

}

private object MasterRouteFactoryTest extends RouteTestCompanion {

  val CORS_ENDPOINTS = List(
    RestVersionsRouteTest,
    UserRouteTest
  ).map(_.CORS_ENDPOINTS).reduce(_ ++ _)

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:21 PM
 */

package base.rest.route

import base.rest.apiKey.ApiKeysRouteTest
import base.rest.auth.AuthRouteTest
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
    ApiKeysRouteTest,
    AuthRouteTest,
    RestVersionsRouteTest,
    UserRouteTest
  ).map(_.CORS_ENDPOINTS).reduce(_ ++ _)

}

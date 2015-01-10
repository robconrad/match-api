/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 10:04 AM
 */

package base.entity.api

import base.entity.test.EntityBaseSuite

/**
 * {{ Describe the high level purpose of ApiVersionsTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ApiVersionsTest extends EntityBaseSuite {

  test("size") {
    assert(ApiVersions.values.size == 1)
  }

  test("latest") {
    assert(ApiVersions.latest == ApiVersions.V01)
  }

  test("available") {
    assert(ApiVersions.available == List(ApiVersions.V01))
  }

  test("asString") {
    val v01: String = ApiVersions.V01
    assert(v01 == "0.1")
  }

}

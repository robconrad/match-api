/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.rest.api

import base.common.service.{ ApiService, ServiceCompanion }

/**
 * Responsible for configuring, starting, binding, etc. the REST API
 * @author rconrad
 */
trait RestApiService extends ApiService {

  final val serviceManifest = manifest[RestApiService]

  val name = "rest-api"

  /**
   * REST Server Connection Information
   */
  def protocol: String
  def host: String
  def port: Int
  def url: String

}

object RestApiService extends ServiceCompanion[RestApiService]

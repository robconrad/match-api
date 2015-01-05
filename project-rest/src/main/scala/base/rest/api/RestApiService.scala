/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:12 PM
 */

package base.rest.api

import base.common.service.{ ApiService, Service, ServiceCompanion }

import scala.concurrent.Future

/**
 * Responsible for configuring, starting, binding, etc. the REST API
 * @author rconrad
 */
trait RestApiService extends ApiService {

  final def serviceManifest = manifest[RestApiService]

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

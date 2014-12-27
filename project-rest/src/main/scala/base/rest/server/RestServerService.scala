/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.server

import base.common.service.{ Service, ServiceCompanion }

import scala.concurrent.Future

/**
 * Responsible for configuring, starting, binding, etc. the REST API
 * @author rconrad
 */
private[rest] trait RestServerService extends Service {

  final def serviceManifest = manifest[RestServerService]

  /**
   * REST Server Connection Information
   */
  def protocol: String
  def host: String
  def port: Int
  def url: String

  /**
   * Start the REST server and bind to the configured port
   */
  def start(): Future[Unit]

}

private[rest] object RestServerService extends ServiceCompanion[RestServerService]

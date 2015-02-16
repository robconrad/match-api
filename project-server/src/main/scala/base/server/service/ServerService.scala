/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.server.service

import base.common.service.{ ApiService, ServiceCompanion }

/**
 * Responsible for configuring, starting, binding, etc. the APIs
 * @author rconrad
 */
trait ServerService extends ApiService {

  final val serviceManifest = manifest[ServerService]

  val name = "server-api"

}

object ServerService extends ServiceCompanion[ServerService]

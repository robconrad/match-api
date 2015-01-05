/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:59 PM
 */

package base.entity.auth

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.UserAuthContext

import scala.concurrent.Future

/**
 * Manage API Request Authentication
 * @author rconrad
 */
trait AuthService extends Service {

  final def serviceManifest = manifest[AuthService]

  /**
   * Accept auth of the allowed types and return proper AuthContext in response
   */
  def auth(token: String, deviceId: String): Future[Option[UserAuthContext]]

}

object AuthService extends ServiceCompanion[AuthService]

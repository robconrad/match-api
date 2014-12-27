/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:11 PM
 */

package base.entity.auth

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.{ AuthContextParams, KeyAuthContext, UserAuthContext }
import base.entity.model.Email

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
  def authByUser(email: Email,
                 password: String,
                 contextParams: AuthContextParams = AuthContextParams()): Future[Option[UserAuthContext]]

  /**
   * Accept auth of the allowed types and return proper AuthContext in response
   */
  def authByKey(key: String,
                contextParams: AuthContextParams = AuthContextParams()): Future[Option[KeyAuthContext]]

}

object AuthService extends ServiceCompanion[AuthService]

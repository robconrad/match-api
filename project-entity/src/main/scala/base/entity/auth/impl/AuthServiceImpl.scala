/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:59 PM
 */

package base.entity.auth.impl

import base.common.logging.TokenLoggable
import base.common.service.ServiceImpl
import base.entity.auth._

import scala.concurrent.Future

/**
 * Manager API Request Authentication
 * @author rconrad
 */
private[entity] class AuthServiceImpl() extends ServiceImpl with AuthService with TokenLoggable {

  /**
   * Accept auth of the allowed types and return proper AuthContext in response
   *  Always returns none since we don't yet support user-level auth
   */
  def auth(token: String, deviceId: String) = Future.successful(None)

}

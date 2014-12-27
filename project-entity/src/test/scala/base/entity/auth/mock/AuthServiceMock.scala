/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 10:16 AM
 */

package base.entity.auth.mock

import base.common.service.ServiceImpl
import base.common.test.TestExceptions.TestRuntimeException
import base.entity.auth._
import base.entity.auth.context.{ KeyAuthContext, AuthContext, AuthContextParams, UserAuthContext }
import base.entity.model.Email

import scala.concurrent.Future

/**
 * Auth mock will return whatever AuthContext you supply, or None
 * @author rconrad
 */
class AuthServiceMock(private var authContext: Option[AuthContext] = None) extends ServiceImpl with AuthService {

  private var fail = false

  /**
   * Convenience for no-option
   */
  def this(authContext: AuthContext) {
    this(Option(authContext))
  }

  /**
   * Accept auth of the allowed types and return proper AuthContext in response
   */
  def authByUser(user: Email, pass: String, contextParams: AuthContextParams) = Future {
    throwFail()
    authContext match {
      case Some(authContext: UserAuthContext) => Option(authContext)
      case _                                  => None
    }
  }

  /**
   * Accept auth of the allowed types and return proper AuthContext in response
   */
  def authByKey(key: String, contextParams: AuthContextParams) = Future {
    throwFail()
    authContext match {
      case Some(authContext: KeyAuthContext) => Option(authContext)
      case _                                 => None
    }
  }

  /**
   * Convenience for no-option
   */
  def setAuthContext(authContext: AuthContext) {
    setAuthContext(Option(authContext))
  }

  /**
   * Update AuthContext value
   */
  def setAuthContext(authContext: Option[AuthContext]) {
    this.authContext = authContext
  }

  /**
   * Cause auth context to blow up when requested
   */
  def failNext() {
    fail = true
  }

  private def throwFail() {
    if (fail) {
      fail = false
      throw new TestRuntimeException("fail auth context")
    }
  }

}

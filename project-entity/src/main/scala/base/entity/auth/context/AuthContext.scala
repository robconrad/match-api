/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:14 PM
 */

package base.entity.auth.context

import base.common.logging.LoggerToken
import base.entity.Tables.UserRow
import base.entity.auth.AuthRoles.AuthRole
import base.entity.auth.AuthTypes.AuthType
import base.entity.perm.PermSetGroup
import base.entity.perm.Perms.Perm

/**
 * Context passed around with API requests indicating authentication status and related info
 * @author rconrad
 */
trait AuthContext {

  /**
   * Whether this AuthContext profile has access to a given permission
   */
  final def has(perm: Perm) = perms.contains(perm)

  /**
   * Group of permissions associated with this AuthContext profile
   */
  def perms: PermSetGroup

  /**
   * Can be used to assert sanity conditions upon creation
   */
  def assertIsValid() {}

  /**
   * The user associated with the AuthContext
   */
  def user: Option[UserRow]

  /**
   * What role of authentication does this context represent
   */
  def authRole: AuthRole

  /**
   * What type of authentication was used to obtain this AuthContext
   */
  def authType: AuthType

  /**
   * The unique identifier of the type of authentication used to obtain this auth context
   */
  def authTypeId: Option[Long]

  /**
   * Provides suite of useful methods for auth contexts
   */
  final lazy val utilities = AuthContextUtilities(this)

  /**
   * Logger token that uniquely identifies this particular AuthContext (i.e. will change from request to request)
   */
  final lazy val token = LoggerToken()

  /**
   * Short unique description of this AuthContext useful for prefixing log events associated with it
   */
  final lazy val toLogPrefix =
    "AuthCtx(%s: %s)".format(
      authType.toPrefix,
      authTypeId.getOrElse(-1L))

}

object AuthContext {

  implicit def authContext2AuthContextUtilities(authCtx: AuthContext) = authCtx.utilities

  object ExceptionStrings {

    val assertIsUser = "assert isUser failed"
    val assertHasNoUser = "assert !hasUser failed"
    val userThrows = "user missing"

  }

}


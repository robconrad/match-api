/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 6:36 PM
 */

package base.entity.auth.context

import java.util.UUID

import base.common.logging.LoggerToken
import base.entity.perm.PermSetGroup
import base.entity.user.User

/**
 * Context passed around with API requests indicating authentication status and related info
 * @author rconrad
 */
trait AuthContext {

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
  def user: Option[User]

  /**
   * The unique identifier of the type of authentication used to obtain this auth context
   */
  def authTypeId: Option[_]

  /**
   * Groups that this user is a member of
   */
  def groups: Set[UUID]

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
  final lazy val toLogPrefix = "AuthCtx(%s)".format(authTypeId.getOrElse(-1L))

}

object AuthContext {

  implicit def authContext2AuthContextUtilities(authCtx: AuthContext): AuthContextUtilities = authCtx.utilities

  object ExceptionStrings {

    val assertIsUser = "assert isUser failed"
    val assertHasNoUser = "assert !hasUser failed"
    val assertHasPerm = "assert has(%s) failed"
    val assertHasGroup = "assert has group %s failed"
    val userThrows = "user missing"

  }

}


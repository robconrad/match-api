/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 6:51 PM
 */

package base.entity.auth.context

import base.entity.auth.context.AuthContext.ExceptionStrings
import base.entity.perm.PermException
import base.entity.perm.Perms.Perm

/**
 * Generally useful methods that extend the functionality of AuthContexts
 * @author rconrad
 */
case class AuthContextUtilities(authCtx: AuthContext) {

  import authCtx._

  /**
   * Whether this AuthContext profile has access to a given permission
   */
  final def has(perm: Perm) = perms.contains(perm)

  /**
   * Whether this is a user themselves
   *  (as opposed to being associated with a user)
   */
  final def isUser = hasUser

  /**
   * Whether this is associated with a user
   */
  final def hasUser = user.isDefined

  /**
   * Throw if not has perm
   */
  final def assertHas(perm: Perm) {
    if (!has(perm)) {
      throw new PermException(ExceptionStrings.assertHasPerm.format(perm))
    }
  }

  /**
   * Throw if not user
   */
  final def assertIsUser() =
    if (!isUser) throw new PermException(ExceptionStrings.assertIsUser)

  /**
   * Throw if not user
   */
  final def assertHasUser() {
    userThrows
  }
  final def assertHasNoUser() =
    if (hasUser) throw new PermException(ExceptionStrings.assertHasNoUser)

  /**
   * user id accessors that throw perm exception on object not found.
   *  These are intended for use in places where perms have already been validated that would cause this not to throw.
   *  Generating stacktrace and throwing these is costly, don't call them unless you don't ever expect them to throw.
   */
  final def userThrows = user.getOrElse(throw new PermException(ExceptionStrings.userThrows))

  /**
   * user id accessors that throw perm exception on object not found.
   */
  final def userId = userThrows.id

  /**
   * user id accessors that throw perm exception on object not found.
   */
  final def userIdOrElse = user.map(_.id).getOrElse(-1L)

}

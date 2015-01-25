/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:53 PM
 */

package base.entity.auth.context

import base.entity.auth.context.AuthContext.ExceptionStrings
import base.entity.perm.{ PermSetGroups, PermException, PermSetGroup }
import base.entity.test.EntityBaseSuite

/**
 * Tests that AuthContext leaves (case classes inheriting ultimately from AuthContext) have the correct attributes and
 *  inheritance structure.
 * @author rconrad
 */
// scalastyle:off line.size.limit
class AuthContextTest extends EntityBaseSuite {

  private def interceptPermException(criteria: Boolean, f: => Unit, msg: String) {
    criteria match {
      case true => f
      case false =>
        val e = intercept[PermException](f)
        assert(e.getMessage == msg)
    }
  }

  // scalastyle:off parameter.number
  // scalastyle:off method.length
  // scalastyle:off cyclomatic.complexity
  private def assertContext(
    ctx: AuthContext,
    authTypeId: Option[_],
    perms: PermSetGroup,
    isUser: Boolean = true,
    hasUser: Boolean = true) {

    perms.permSet.set.foreach { perm =>
      assert(ctx.has(perm))
      ctx.assertHas(perm)
    }
    assert(ctx.perms == perms)

    ctx.assertIsValid()

    assert(ctx.isInstanceOf[UserAuthContext] == isUser)

    assert(ctx.isUser == isUser)

    interceptPermException(isUser, ctx.assertIsUser(), ExceptionStrings.assertIsUser)

    assert(ctx.hasUser == hasUser)

    interceptPermException(hasUser, ctx.assertHasUser(), ExceptionStrings.userThrows)

    interceptPermException(!hasUser, ctx.assertHasNoUser(), ExceptionStrings.assertHasNoUser)

    if (!hasUser) {
      assert(ctx.user == None)
    }

    interceptPermException(hasUser, ctx.userThrows, ExceptionStrings.userThrows)
    interceptPermException(hasUser, ctx.userId, ExceptionStrings.userThrows)

    authTypeId match {
      case Some(id) => assert(ctx.userIdOrElse == id)
      case None     => assert(ctx.userIdOrElse == -1)
    }

    assert(ctx.authTypeId == authTypeId)
  }
  // scalastyle:on parameter.number
  // scalastyle:on method.length
  // scalastyle:on cyclomatic.complexity

  private implicit def authId2Option(authId: Long) = Option(authId)

  test("UserAuthContext") {
    assertContext(
      ChannelContextDataFactory.userAuth.authCtx,
      ChannelContextDataFactory.userAuth.authCtx.user.map(_.id),
      PermSetGroups.user)
  }

  test("NoAuthContext") {
    assertContext(
      NoAuthContext,
      None,
      PermSetGroups.public,
      isUser = false,
      hasUser = false)
  }

}

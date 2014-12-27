/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:28 AM
 */

package base.entity.auth.impl

import base.common.test.UniqueTestDataFactory
import base.entity.auth.context._
import base.entity.service.EntityServiceTest
import base.entity.user.UserDataFactory
import org.scalatest.concurrent.AsyncAssertions.Waiter

import scala.util.{ Failure, Success }

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class AuthServiceImplUsersTest() extends EntityServiceTest {

  override protected val shouldSetupAndCleanDb = true

  val service = new AuthServiceImpl()

  // scalastyle:off method.length
  // scalastyle:off cyclomatic.complexity
  private def testAuth(userActive: Boolean = true,
                       passwordCorrect: Boolean = true,
                       userExists: Boolean = true,
                       success: Boolean = true) {
    val w = new Waiter

    val actualEmail = UniqueTestDataFactory.string("email")
    val sentEmail = userExists match {
      case true  => actualEmail
      case false => "wrong email"
    }

    val actualPassword = UniqueTestDataFactory.string("password")
    val sentPassword = passwordCorrect match {
      case true  => actualPassword
      case false => "wrong password"
    }

    val user = userExists match {
      case true  => Option(UserDataFactory(actualEmail, actualPassword, userActive))
      case false => None
    }

    val authCtx: Option[AuthContext] = user.map(StandardUserAuthContext.apply)

    val contextParams = AuthContextParams()

    val expected = success match {
      case true  => authCtx
      case false => None
    }

    service.authByUser(sentEmail, sentPassword, contextParams).onComplete { result =>
      w {
        result match {
          case Success(actual) =>
            debug(s"expected: $expected")
            debug(s"  actual: $actual")
            assert(actual == expected)
          case Failure(e) => fail(e)
        }
      }
      w.dismiss()
    }
    w.await()
  }
  // scalastyle:on method.length
  // scalastyle:on cyclomatic.complexity

  test("all active, correct password") {
    testAuth()
  }

  test("user inactive") {
    testAuth(userActive = false, success = false)
  }

  test("incorrect password") {
    testAuth(passwordCorrect = false, success = false)
  }

  test("user doesn't exist") {
    testAuth(userExists = false, success = false)
  }

}

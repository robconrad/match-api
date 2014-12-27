/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:29 AM
 */

package base.entity.auth.impl

import base.common.lib._
import base.common.logging.{ LoggerToken, TokenLoggable }
import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.entity.Tables._
import base.entity.Tables.profile.simple._
import base.entity.auth._
import base.entity.auth.context._
import base.entity.db.DbService
import base.entity.auth.impl.AuthServiceImpl._
import base.entity.model.Email
import com.google.common.hash.Hashing

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
  def authByUser(email: Email, password: String, contextParams: AuthContextParams) = {
    implicit val t = token
    implicit val cp = contextParams
    debug("authByUser: email: %s, context: %s", email, contextParams)
    val authCtx = getAuthContextFromUser(email, password)
    assertIsValid(authCtx)
  }

  /**
   * Accept auth of the allowed types and return proper AuthContext in response
   */
  def authByKey(key: String, contextParams: AuthContextParams) = {
    implicit val t = token
    implicit val cp = contextParams
    debug("authByKey: context: %s", contextParams)
    val authCtx = getAuthContextFromKey(key)
    assertIsValid(authCtx)
  }

  /**
   * Ensure the generated AuthContext is valid under its own rules
   */
  private def assertIsValid[T <: AuthContext](f: Future[Option[T]]) = {
    f.map(_.map { authCtx =>
      authCtx.assertIsValid()
      authCtx
    })
  }

  private def getAuthContextFromUser(email: Email,
                                     password: String)(implicit t: LoggerToken,
                                                       ctxParams: AuthContextParams): Future[Option[UserAuthContext]] =
    DbService().withTxResultInternal { implicit s =>
      val q = for {
        u <- Users
        if u.email === email.v
        if u.active === true
      } yield u
      q.firstOption match {
        case Some(user) =>
          user.passwordHash == getPasswordHash(password, user.passwordSalt) match {
            case true =>
              Option(StandardUserAuthContext(user))
            case false =>
              debug("getAuthContextFromUser reports password did not match for user: %s", user)
              None
          }
        case _ =>
          debug("getAuthContextFromUser failed to find active user with email: %s", email)
          None
      }
    }

  private def getAuthContextFromKey(key: String)(implicit t: LoggerToken,
                                                 ctxParams: AuthContextParams): Future[Option[KeyAuthContext]] =
    DbService().withTxResultInternal { implicit s =>
      val q = for {
        k <- ApiKeys
        if k.value === key
        if k.active === true
      } yield k
      q.firstOption.map(StandardKeyAuthContext.apply)
    }

}

object AuthServiceImpl {

  /**
   * Convert a password & salt into a salted password hash
   */
  def getPasswordHash(password: String, salt: String): String = {
    Hashing.sha256().hashString(password + salt, Encoding.CHARSET_UTF8).toString
  }

  /**
   * Generate a random password salt
   */
  def getPasswordSalt = {
    RandomService().sha256.toString
  }

}

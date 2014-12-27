/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.db.impl

import base.common.service.ServiceImpl
import base.entity.Tables.profile.simple._
import base.entity.db.{ DbConfigService, DbService }
import base.entity.error.ApiError

import scala.concurrent.{ Future, Promise }

/**
 * Database Access Manager
 *
 * ### Futures ###
 * I know this looks stupid, what is the point of wrapping everything in an anonymous
 * future? The answer is that at some point in the near future Slick 2.2 will be
 * released and we will finally have support for non-blocking db calls. Until then
 * we pretend db calls are non-blocking so that all code that accesses the DbService
 * is built in a non-blocking way.
 *
 * ### Transactions ###
 * Note that if any query throws within a transaction it will be aborted in its
 * entirety (i.e. try/catch SQLException within a transaction won't help you out)
 *
 * @author rconrad
 */
private[entity] class DbServiceImpl() extends ServiceImpl with DbService {

  // scalastyle:off null
  private val db = Database.forURL(
    DbConfigService().url,
    DbConfigService().user,
    DbConfigService().pass,
    prop = null,
    DbConfigService().driver)
  // scalastyle:on

  /**
   * All DB queries that have side effects and no result value must return EntityError
   * on failure when run in the context of the API
   */
  def withTx(f: Session => Option[ApiError]) =
    withTxResultInternal(f)

  /**
   * All DB queries that have side effects and a result value must return Either
   * EntityError on failure or Result on success when run in the context of the API
   */
  def withTxResult[T](f: Session => Either[ApiError, T]) =
    withTxResultInternal(f)

  /**
   * All DB queries that have side effects and a result value must return Either
   * EntityError on failure or Result on success when run in the context of the API
   */
  def withTxResultFuture[T](f: Session => Future[Either[ApiError, T]]) =
    withTxResultInternal(f).flatMap(identity)

  /**
   * Internal (i.e. non-API-context) DB queries may be run without returning EntityError
   */
  def withTxInternal(f: Session => Unit) =
    withTxResultInternal(f)

  /**
   * Internal (i.e. non-API-context) DB queries may be run without returning EntityError
   */
  def withTxResultInternal[T](f: Session => T) = {
    val p = Promise[T]()
    try {
      val r = db withTransaction f
      p success r
    } catch {
      case e: Throwable =>
        error("db transaction failed with ", e)
        p failure e
    }
    p.future
  }

}

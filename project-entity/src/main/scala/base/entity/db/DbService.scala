/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.db

import base.common.service.{ Service, ServiceCompanion }
import base.entity.Tables.profile.simple._
import base.entity.error.ApiError

import scala.concurrent.Future

/**
 * Database Access Manager
 *
 * Transactions - note that if any query throws within a transaction it will be aborted in its
 * entirety (i.e. try/catch SQLException within a transaction won't help you out)
 *
 * @author rconrad
 */
private[entity] trait DbService extends Service {

  final def serviceManifest = manifest[DbService]

  /**
   * All DB queries that have side effects and no result value must return EntityError
   * on failure when run in the context of the API
   */
  def withTx(f: Session => Option[ApiError]): Future[Option[ApiError]]

  /**
   * All DB queries that have side effects and a result value must return Either
   * EntityError on failure or Result on success when run in the context of the API
   */
  def withTxResult[T](f: Session => Either[ApiError, T]): Future[Either[ApiError, T]]

  /**
   * All DB queries that have side effects and a result value must return Either
   * EntityError on failure or Result on success when run in the context of the API
   */
  def withTxResultFuture[T](f: Session => Future[Either[ApiError, T]]): Future[Either[ApiError, T]]

  /**
   * Internal (i.e. non-API-context) DB queries may be run without returning EntityError
   */
  def withTxInternal(f: Session => Unit): Future[Unit]

  /**
   * Internal (i.e. non-API-context) DB queries may be run without returning EntityError
   */
  def withTxResultInternal[T](f: Session => T): Future[T]

}

object DbService extends ServiceCompanion[DbService]

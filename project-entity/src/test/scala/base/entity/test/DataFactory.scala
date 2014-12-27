/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:07 PM
 */

package base.entity.test

import base.common.lib.Dispatchable
import base.common.test.PimpMyFutures
import base.common.time.DateTimeHelper
import base.entity.Tables.profile.simple._
import base.entity.apiKey.ApiKeyDataFactory
import base.entity.db.DbService
import base.entity.user.UserDataFactory

/**
 * Objects responsible for creating test data for a single database table
 *  (I really wanted to make a generic cleanup method by specifying an abstract table val,
 *   but alas, Slick is doing some batshit bananas stuff with the type system making this.. difficult)
 * @author rconrad
 */
trait DataFactory extends PimpMyFutures with DateTimeHelper {

  /**
   * Cleanup data in the table this factory is responsible for
   * (block on each so we don't attempt to go in parallel and get referential integrity errors)
   */
  final def cleanup() {
    DbService().withTxInternal(cleanup(_)).await()
  }

  /**
   * Actual clean implementation
   */
  protected def cleanup(implicit s: Session)

}

object DataFactory extends Dispatchable with PimpMyFutures {

  /**
   * Registry of factories - MUST be in order of FK chain in order for deletes to work properly
   */
  val factories = List(
    UserDataFactory,
    ApiKeyDataFactory
  )

  /**
   * Delete data from all tables that have data factories
   */
  def cleanup() = factories.foreach(_.cleanup())

}

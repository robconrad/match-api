/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.db

import base.common.logging.Loggable
import base.common.test.PimpMyFutures
import base.entity.db.DbConfigService.{ H2, Postgres }
import base.entity.test.DataFactory

import scala.slick.jdbc.StaticQuery.interpolation

/**
 * Provides methods that assist in testing classes for which the database cannot be mocked out
 *  (i.e. which are database interactors, usually services or actors)
 * @author rconrad
 */
private[entity] trait DbTestHelper extends PimpMyFutures with Loggable {

  /**
   * Setup the database (create tables, etc.)
   */
  protected def setupDb() {
    info("setup db")
    EvolutionService().evolve().awaitLong()
  }

  /**
   * Delete all data from the database (except evolutions) and leave structure intact
   */
  protected def cleanDb() {
    info("clean db")
    DataFactory.cleanup()
  }

  /**
   * Completely remove everything from the database
   */
  protected def destroyDb() {
    DbService().withTxInternal { implicit s =>
      val sql = DbConfigService().dbType match {
        case H2       => sqlu"""DROP ALL OBJECTS;"""
        case Postgres => sqlu"""DROP SCHEMA public CASCADE; CREATE SCHEMA public; CREATE EXTENSION "uuid-ossp";"""
      }
      info(s"destroying db with $sql")
      sql.execute
    }.awaitLong()
  }

}

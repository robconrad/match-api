/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.db

import base.common.service.{ Service, ServiceCompanion }
import base.entity.db.DbConfigService.DbType

import scala.slick.driver.JdbcDriver

/**
 * Database Configuration Manager
 *
 * Separate from DB Access Manager (DbService) because config inside that server would cause a cyclic dependency
 *  with the Tables definition (since it uses a config value to determine which JDBC driver to import)
 *
 * @author rconrad
 */
private[entity] trait DbConfigService extends Service {

  final def serviceManifest = manifest[DbConfigService]

  /**
   * What DB are we running? (a handful of times we may need to vary behavior)
   */
  def dbType: DbType

  /**
   * Runtime JdbcDriver to use for slick queries
   */
  def dbDriver: JdbcDriver

  /**
   * DB Configuration values available only to other DB services
   */
  private[db] def driver: String
  private[db] def url: String
  private[db] def user: String
  private[db] def pass: String

}

object DbConfigService extends ServiceCompanion[DbConfigService] {

  sealed trait DbType
  case object H2 extends DbType
  case object Postgres extends DbType

}

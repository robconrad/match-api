/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.db.impl

import base.common.service.ServiceImpl
import base.entity.db.DbConfigService
import base.entity.db.DbConfigService.{ H2, Postgres }

/**
 * Database Configuration Manager
 *
 * Separate from DB Access Manager (DbService) because config inside that server would cause a cyclic dependency
 * with the Tables definition (since it uses a config value to determine which JDBC driver to import)
 *
 * @author rconrad
 */
private[entity] class DbConfigServiceImpl(private[db] val driver: String,
                                          private[db] val url: String,
                                          private[db] val user: String,
                                          private[db] val pass: String) extends ServiceImpl with DbConfigService {

  /**
   * What DB are we running? (a handful of times we may need to vary behavior)
   */
  val dbType = {
    if ("org.h2.Driver" == driver) {
      H2
    } else if ("org.postgresql.Driver" == driver) {
      Postgres
    } else {
      throw new Exception("unknown database type")
    }
  }

  /**
   * Runtime JdbcDriver to use for slick queries
   */
  val dbDriver = dbType match {
    case H2       => scala.slick.driver.JdbcDriver
    case Postgres => scala.slick.driver.PostgresDriver
  }

}

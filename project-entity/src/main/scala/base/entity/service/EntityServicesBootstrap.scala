/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:06 PM
 */

package base.entity.service

import base.common.service.{ Services, ServicesBootstrap }
import base.entity.apiKey.impl.ApiKeysServiceImpl
import base.entity.auth.impl.AuthServiceImpl
import base.entity.db.impl.{ DbConfigServiceImpl, DbServiceImpl, EvolutionServiceImpl }
import base.entity.user.impl.UserServiceImpl

/**
 * Injects configuration into Services and boots them up. If it's configurable, it belongs here.
 * @author rconrad
 */
object EntityServicesBootstrap extends ServicesBootstrap {

  /**
   * Config Sections
   */
  protected val DB = "db"

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = dbRegistered && otherRegistered

  /**
   * Register DB services (must come first)
   */
  lazy val dbRegistered = {

    Services.register(new DbConfigServiceImpl(
      Keys(DB, "driver"),
      Keys(DB, "url"),
      Keys(DB, "user"),
      Keys(DB, "pass")
    ))

    Services.register(new DbServiceImpl())

    Services.register(new EvolutionServiceImpl(
      Keys(DB, "evolution")
    ))

    true
  }

  /**
   * Register all non-DB services
   */
  lazy val otherRegistered = {

    Services.register(new AuthServiceImpl())

    Services.register(new UserServiceImpl())

    Services.register(new ApiKeysServiceImpl())

    true
  }

}

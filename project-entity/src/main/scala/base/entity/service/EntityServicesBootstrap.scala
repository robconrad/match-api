/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:24 PM
 */

package base.entity.service

import base.common.service.{ Services, ServicesBootstrap }
import base.entity.auth.impl.AuthServiceImpl
import base.entity.kv.impl.KvServiceImpl
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
  protected val KV = "kv"

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = kvRegistered && otherRegistered

  /**
   * Register KV services (must come first)
   */
  lazy val kvRegistered = {

    Services.register(new KvServiceImpl(
      Keys(KV, "clientCount"),
      Keys(KV, "host"),
      Keys(KV, "port")))

    true
  }

  /**
   * Register all non-DB services
   */
  lazy val otherRegistered = {

    Services.register(new AuthServiceImpl())

    Services.register(new UserServiceImpl())

    true
  }

}

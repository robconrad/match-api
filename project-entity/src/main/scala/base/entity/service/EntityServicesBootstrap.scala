/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 9:39 AM
 */

package base.entity.service

import base.common.service.{ Services, ServicesBootstrap }
import base.entity.auth.impl.AuthServiceImpl
import base.entity.kv.impl.KvServiceImpl
import base.entity.sms.impl.TwilioSmsServiceImpl
import base.entity.user.impl.RegisterServiceImpl

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
  protected val TWILIO = "twilio"

  protected val MATCH = "match"
  protected val MATCH_USER = Keys(MATCH, "user")

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

    Services.register(new TwilioSmsServiceImpl(
      Keys(TWILIO, "sid"),
      Keys(TWILIO, "token"),
      Keys(TWILIO, "from")))

    Services.register(new RegisterServiceImpl(
      Keys(MATCH_USER, "phoneCooldown"),
      Keys(MATCH_USER, "verifyCodeLength"),
      Keys(MATCH_USER, "verifySmsBody")))

    true
  }

}

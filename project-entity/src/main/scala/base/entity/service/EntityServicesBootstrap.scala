/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:10 PM
 */

package base.entity.service

import base.common.service.{ Services, ServicesBootstrap }
import base.entity.auth.impl.AuthServiceImpl
import base.entity.kv.impl.KvFactoryServiceImpl
import base.entity.group.impl.InviteCommandServiceImpl
import base.entity.sms.impl.TwilioSmsServiceImpl
import base.entity.user.impl._

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
  protected val MATCH_GROUP = Keys(MATCH, "group")

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = kvRegistered && otherRegistered

  /**
   * Register KV services (must come first)
   */
  lazy val kvRegistered = {

    Services.register(new KvFactoryServiceImpl(
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

    Services.register(new RegisterCommandServiceImpl(
      Keys(MATCH_USER, "phoneCooldown")))

    Services.register(new VerifyCommandServiceImpl(
      Keys(MATCH_USER, "verifyCodeLength"),
      Keys(MATCH_USER, "verifySmsBody")))

    Services.register(new LoginCommandServiceImpl())

    Services.register(new InviteCommandServiceImpl(
      Keys(MATCH_GROUP, "inviteSmsBody")))

    Services.register(new UserKeyServiceImpl())
    Services.register(new DeviceKeyServiceImpl())
    Services.register(new PhoneKeyServiceImpl())
    Services.register(new PhoneCooldownKeyServiceImpl())

    true
  }

}

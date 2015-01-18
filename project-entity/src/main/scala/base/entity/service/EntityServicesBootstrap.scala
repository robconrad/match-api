/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 6:59 PM
 */

package base.entity.service

import base.common.service.{ Services, ServicesBootstrap }
import base.entity.group.impl.{ GroupEventsServiceImpl, GroupServiceImpl, InviteCommandServiceImpl }
import base.entity.group.kv.impl._
import base.entity.kv.impl.KvFactoryServiceImpl
import base.entity.message.impl.MessageCommandServiceImpl
import base.entity.sms.impl.TwilioSmsServiceImpl
import base.entity.user.impl._
import base.entity.user.kv.impl._

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
  protected val MATCH_GROUP_EVENT = Keys(MATCH_GROUP, "event")

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

    Services.register(new TwilioSmsServiceImpl(
      Keys(TWILIO, "sid"),
      Keys(TWILIO, "token"),
      Keys(TWILIO, "from")))

    Services.register(new GroupServiceImpl())

    Services.register(new GroupEventsServiceImpl(
      Keys(MATCH_GROUP_EVENT, "count")))

    Services.register(new UserServiceImpl())

    Services.register(new RegisterCommandServiceImpl(
      Keys(MATCH_USER, "phoneCooldown")))

    Services.register(new VerifyCommandServiceImpl(
      Keys(MATCH_USER, "verifyCodeLength"),
      Keys(MATCH_USER, "verifySmsBody")))

    Services.register(new LoginCommandServiceImpl())

    Services.register(new InviteCommandServiceImpl(
      Keys(MATCH_GROUP, "welcomeMessage")))

    Services.register(new MessageCommandServiceImpl())

    Services.register(new UserKeyServiceImpl())
    Services.register(new DeviceKeyServiceImpl())
    Services.register(new PhoneKeyServiceImpl())
    Services.register(new PhoneCooldownKeyServiceImpl())
    Services.register(new UserGroupsKeyServiceImpl())

    Services.register(new GroupKeyServiceImpl())
    Services.register(new GroupPairKeyServiceImpl())
    Services.register(new GroupUserKeyServiceImpl())
    Services.register(new GroupUsersKeyServiceImpl())
    Services.register(new GroupEventsKeyServiceImpl())

    true
  }

}

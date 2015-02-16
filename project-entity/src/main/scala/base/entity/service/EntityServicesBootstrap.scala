/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 6:12 PM
 */

package base.entity.service

import base.common.lib.BaseConfig
import base.common.service.{Services, ServicesBootstrap}
import base.entity.error.impl.ApiErrorServiceImpl
import base.entity.event.impl.AckEventsCommandServiceImpl
import base.entity.facebook.impl.FacebookServiceImpl
import base.entity.group.impl._
import base.entity.kv.impl.{KeyCommandsServiceImpl, KeyFactoryServiceImpl}
import base.entity.message.impl.MessageCommandServiceImpl
import base.entity.question.QuestionDef
import base.entity.question.impl.{AnswerCommandServiceImpl, CreateQuestionCommandServiceImpl, QuestionServiceImpl, QuestionsCommandServiceImpl}
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
  protected val FACEBOOK = "facebook"
  protected val TWILIO = "twilio"

  protected val MATCH = "match"
  protected val MATCH_USER = Keys(MATCH, "user")
  protected val MATCH_GROUP = Keys(MATCH, "group")
  protected val MATCH_GROUP_EVENT = Keys(MATCH_GROUP, "event")
  protected val MATCH_QUESTION = Keys(MATCH, "question")

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = kvRegistered && otherRegistered

  /**
   * Register KV services (must come first)
   */
  lazy val kvRegistered = {

    Services.register(new KeyCommandsServiceImpl(
      Keys(KV, "clientCount"),
      Keys(KV, "host"),
      Keys(KV, "port")))

    true
  }

  /**
   * Register all non-DB services
   */
  lazy val otherRegistered = {

    Services.register(new KeyFactoryServiceImpl())

    Services.register(new ApiErrorServiceImpl(
      Keys(MATCH, "debug")))

    Services.register(new TwilioSmsServiceImpl(
      Keys(TWILIO, "sid"),
      Keys(TWILIO, "token"),
      Keys(TWILIO, "from")))

    Services.register(new FacebookServiceImpl(
      Keys(FACEBOOK, "infoExpireTime")))

    Services.register(new GroupServiceImpl())

    Services.register(new GroupEventsServiceImpl(
      Keys(MATCH_GROUP_EVENT, "count"),
      Keys(MATCH_GROUP_EVENT, "store"),
      Keys(MATCH_GROUP_EVENT, "delta")))

    Services.register(new GroupListenerServiceImpl(
      GroupListenerServiceImpl.makeActor))

    Services.register(new QuestionServiceImpl(
      getConfigList(Keys(MATCH_QUESTION, "questions")).map { tsConfig =>
        implicit val config = new BaseConfig(tsConfig)
        QuestionDef(
          Keys("id"),
          Keys("a"),
          Keys("b"))
      },
      Keys(MATCH_QUESTION, "count"),
      Keys(MATCH_QUESTION, "groupCount")))

    Services.register(new UserServiceImpl())

    Services.register(new RegisterPhoneCommandServiceImpl(
      Keys(MATCH_USER, "phoneCooldown")))

    Services.register(new VerifyPhoneCommandServiceImpl(
      Keys(MATCH_USER, "verifyCodeLength"),
      Keys(MATCH_USER, "verifySmsBody")))

    Services.register(new LoginCommandServiceImpl())

    Services.register(new SendInviteCommandServiceImpl(
      Keys(MATCH_GROUP, "welcomeMessage")))

    Services.register(new AcceptInviteCommandServiceImpl(
      Keys(MATCH_GROUP, "joinMessage")))

    Services.register(new DeclineInviteCommandServiceImpl())

    Services.register(new MessageCommandServiceImpl())

    Services.register(new QuestionsCommandServiceImpl())

    Services.register(new AnswerCommandServiceImpl())

    Services.register(new CreateQuestionCommandServiceImpl(
      Keys(MATCH_QUESTION, "maxUserQuestions")))

    Services.register(new AckEventsCommandServiceImpl())

    true
  }

}

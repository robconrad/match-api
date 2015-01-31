/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/27/15 6:34 PM
 */

package base.entity.facebook.impl

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.facebook.kv.{ FacebookInfoKeyService, FacebookInfoKey }
import base.entity.facebook.{ FacebookInfo, FacebookService }
import base.entity.kv.Key.Pipeline
import base.entity.kv.KvFactoryService
import base.entity.logging.AuthLoggable
import com.restfb.exception.{ FacebookException, FacebookOAuthException }
import com.restfb.json.JsonObject
import com.restfb.{ DefaultFacebookClient, Parameter, Version }
import redis.client.RedisException

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
 * {{ Describe the high level purpose of FacebookServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
class FacebookServiceImpl(infoExpireTime: FiniteDuration)
    extends ServiceImpl
    with FacebookService
    with AuthLoggable {

  def getInfo(token: String)(implicit channelCtx: ChannelContext) = {
    implicit val p = KvFactoryService().pipeline
    new GetInfoMethod(token).execute()
  }

  private[impl] class GetInfoMethod(token: String)(implicit p: Pipeline, ctx: ChannelContext) {

    val fieldId = "id"
    val fieldFirstName = "first_name"
    val fieldGender = "gender"
    val fieldLocale = "locale"
    val objectMe = "me"
    val fieldParameters = Parameter.`with`("fields", s"$fieldId,$fieldFirstName,$fieldGender,$fieldLocale")

    def execute() = {
      getCachedInfo(FacebookInfoKeyService().make(token))
    }

    def getCachedInfo(key: FacebookInfoKey) = {
      key.get.flatMap {
        case Some(info) => Future.successful(Option(info))
        case None       => wrapFetchInfo(key)
      }
    }

    def wrapFetchInfo(key: FacebookInfoKey) = {
      try {
        fetchInfo(key).map(Option.apply)
      } catch {
        case e: FacebookOAuthException =>
          // no stack for simple auth failures - though we do log so we can track any weird uptick
          info("Facebook OAuth failed for token: %s", token)
          Future.successful(None)
        case e: FacebookException =>
          // other facebook failures are warnings.. at least
          warn("Facebook OAuth failed for token: %s with ", e, token)
          Future.successful(None)
      }
    }

    def fetchInfo(key: FacebookInfoKey) = {
      val client = makeClient(token)
      val jsonObject = client.fetchObject(objectMe, classOf[JsonObject], fieldParameters)

      val id = jsonObject getString fieldId
      val firstName = jsonObject getString fieldFirstName
      val gender = jsonObject getString fieldGender
      val locale = jsonObject getString fieldLocale

      val info = FacebookInfo(id, firstName, gender, locale)

      setInfo(key, info)
    }

    def setInfo(key: FacebookInfoKey, info: FacebookInfo) = {
      key.set(info).flatMap {
        case true  => expireInfo(key, info)
        case false => throw new RedisException("failed to set facebook info")
      }
    }

    def expireInfo(key: FacebookInfoKey, info: FacebookInfo) = {
      key.expire(infoExpireTime.toSeconds).map {
        case true  => info
        case false => throw new RedisException("failed to expire facebook info")
      }
    }

  }

  def makeClient(token: String) = new DefaultFacebookClient(token, Version.VERSION_2_2)

}

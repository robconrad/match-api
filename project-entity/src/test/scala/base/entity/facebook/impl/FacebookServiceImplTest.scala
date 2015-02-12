/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:53 PM
 */

package base.entity.facebook.impl

import base.entity.auth.context.ChannelContextDataFactory
import base.entity.facebook.FacebookInfo
import base.entity.facebook.kv.{FacebookInfoKey, FacebookInfoKeyService}
import base.entity.kv.KvTest
import base.entity.test.EntityBaseSuite
import com.restfb.exception.{FacebookException, FacebookOAuthException}
import com.restfb.json.JsonObject
import com.restfb.{DefaultFacebookClient, Parameter}
import redis.client.RedisException

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of FacebookServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
class FacebookServiceImplTest extends EntityBaseSuite with KvTest {

  private val token = "token"
  private val fbInfo = FacebookInfo("", "", "", "")
  private val expireTime = 1.day

  private implicit def service: FacebookServiceImpl = new FacebookServiceImpl(expireTime)
  private def method(implicit service: FacebookServiceImpl) =
    new service.GetInfoMethod(token)(tp, ChannelContextDataFactory.userAuth)

  test("success") {
    implicit val ctx = ChannelContextDataFactory.userAuth
    val client = mock[DefaultFacebookClient]
    val jsonObject = mock[JsonObject]
    implicit val service = new FacebookServiceImpl(expireTime) {
      override def makeClient(token: String) = client
    }

    (client.fetchObject[JsonObject](_: String, _: Class[JsonObject], _: Parameter)) expects
      (method.objectMe, classOf[JsonObject], *) returning jsonObject
    jsonObject.getString _ expects method.fieldId returning ""
    jsonObject.getString _ expects method.fieldFirstName returning ""
    jsonObject.getString _ expects method.fieldGender returning ""
    jsonObject.getString _ expects method.fieldLocale returning ""

    // uncached call (calls fb client mock)
    assert(service.getInfo(token).await() == Option(fbInfo))

    // has set the cache
    val key = FacebookInfoKeyService().make(token)
    assert(key.get.await() == Option(fbInfo))
    assert(key.ttl().await().exists(ttl => ttl > expireTime.toSeconds - 2 && ttl <= expireTime.toSeconds))

    // is now a cached call (fb client mock was only called once)
    assert(service.getInfo(token).await() == Option(fbInfo))
  }

  test("wrapFetchInfo received FacebookOAuthException") {
    implicit val service = new FacebookServiceImpl(expireTime) {
      override def makeClient(token: String) =
        throw new FacebookOAuthException(null, null, null, null, null, null, null)
    }
    val key = mock[FacebookInfoKey]
    assert(method.wrapFetchInfo(key).await() == None)
  }

  test("wrapFetchInfo received FacebookException") {
    implicit val service = new FacebookServiceImpl(expireTime) {
      override def makeClient(token: String) =
        throw new FacebookException(null) {}
    }
    val key = mock[FacebookInfoKey]
    assert(method.wrapFetchInfo(key).await() == None)
  }

  test("wrapFetchInfo received RedisException") {
    implicit val service = new FacebookServiceImpl(expireTime) {
      override def makeClient(token: String) = throw new RedisException()
    }
    val key = mock[FacebookInfoKey]
    intercept[RedisException] {
      method.wrapFetchInfo(key).await()
    }
  }

  test("setInfo failed") {
    val key = mock[FacebookInfoKey]

    key.set _ expects fbInfo returning Future.successful(false)

    intercept[RedisException] {
      method.setInfo(key, fbInfo).await()
    }
  }

  test("expireInfo failed") {
    val key = mock[FacebookInfoKey]

    key.expire _ expects expireTime.toSeconds returning Future.successful(false)

    intercept[RedisException] {
      method.expireInfo(key, fbInfo).await()
    }
  }

  test("makeClient") {
    val client = new FacebookServiceImpl(expireTime).makeClient("token")
    assert(client.isInstanceOf[DefaultFacebookClient])
  }

}

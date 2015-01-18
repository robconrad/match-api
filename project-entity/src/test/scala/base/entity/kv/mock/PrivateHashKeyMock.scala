/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:42 PM
 */

package base.entity.kv.mock

import java.util.UUID

import base.common.random.RandomService
import base.entity.kv.Key.{ Prop, Pipeline }
import base.entity.kv.{ KvFactoryService, PrivateHashKey, Key }
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of IntKeyMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PrivateHashKeyMock(val token: String = RandomService().md5.toString,
                         getStringResult: Future[Option[String]] = Future.successful(None),
                         getDateTimeResult: Future[Option[DateTime]] = Future.successful(None),
                         getIdResult: Future[Option[UUID]] = Future.successful(None),
                         getIntResult: Future[Option[Int]] = Future.successful(None),
                         getLongResult: Future[Option[Long]] = Future.successful(None),
                         getFlagResult: Future[Boolean] = Future.successful(true),
                         getMultiResult: Future[Map[Prop, Option[String]]] = Future.successful(Map()),
                         getResult: Future[Map[Prop, String]] = Future.successful(Map()),
                         getPropsResult: Future[List[Prop]] = Future.successful(List()),
                         setFlagResult: Future[Boolean] = Future.successful(true),
                         setResult: Future[Boolean] = Future.successful(true),
                         setMultiResult: Future[Boolean] = Future.successful(true),
                         setNxResult: Future[Boolean] = Future.successful(true),
                         incrementResult: Future[Long] = Future.successful(1L),
                         delResult: Future[Boolean] = Future.successful(true),
                         delMultiResult: Future[Boolean] = Future.successful(true),
                         keyMock: Key = new KeyMock()(KvFactoryService().pipeline))(implicit protected val p: Pipeline)
    extends PrivateHashKey {

  def getString(prop: Prop) = getStringResult

  def getDateTime(prop: Prop) = getDateTimeResult

  def getId(prop: Prop) = getIdResult

  def getInt(prop: Prop) = getIntResult

  def getLong(prop: Prop) = getLongResult

  def getFlag(prop: Prop) = getFlagResult

  def get(props: Array[Prop]) = getMultiResult

  def get = getResult

  def getProps = getPropsResult

  def setFlag(prop: Prop, value: Boolean) = setFlagResult

  def set(prop: Prop, value: Any) = setResult

  def set(props: Map[Prop, Any]) = setMultiResult

  def setNx(prop: Prop, value: Any) = setNxResult

  def increment(prop: Prop, value: Long) = incrementResult

  def del(prop: Prop) = delResult

  def del(props: List[Prop]) = delMultiResult

  def exists() = keyMock.exists()

  def expire(seconds: Long) = keyMock.expire(seconds)

  def ttl() = keyMock.ttl()

  def del() = keyMock.del()

}

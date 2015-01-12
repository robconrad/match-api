/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 4:53 PM
 */

package base.entity.kv.mock

import java.util.UUID

import base.common.random.RandomService
import base.entity.kv.Key.{ Prop, Pipeline }
import base.entity.kv.{ HashKey, Key }
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of IntKeyMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class HashKeyMock(val token: String = RandomService().md5.toString,
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
                  keyMock: Key = new KeyMock()) extends HashKey {

  def getString(prop: Prop)(implicit p: Pipeline) = getStringResult

  def getDateTime(prop: Prop)(implicit p: Pipeline) = getDateTimeResult

  def getId(prop: Prop)(implicit p: Pipeline) = getIdResult

  def getInt(prop: Prop)(implicit p: Pipeline) = getIntResult

  def getLong(prop: Prop)(implicit p: Pipeline) = getLongResult

  def getFlag(prop: Prop)(implicit p: Pipeline) = getFlagResult

  def get(props: Array[Prop])(implicit p: Pipeline) = getMultiResult

  def get(implicit p: Pipeline) = getResult

  def getProps(implicit p: Pipeline) = getPropsResult

  def setFlag(prop: Prop, value: Boolean)(implicit p: Pipeline) = setFlagResult

  def set(prop: Prop, value: Any)(implicit p: Pipeline) = setResult

  def set(props: Map[Prop, Any])(implicit p: Pipeline) = setMultiResult

  def setNx(prop: Prop, value: Any)(implicit p: Pipeline) = setNxResult

  def increment(prop: Prop, value: Long)(implicit p: Pipeline) = incrementResult

  def del(prop: Prop)(implicit p: Pipeline) = delResult

  def del(props: List[Prop])(implicit p: Pipeline) = delMultiResult

  def exists()(implicit p: Pipeline) = keyMock.exists()

  def expire(seconds: Long)(implicit p: Pipeline) = keyMock.expire(seconds)

  def ttl()(implicit p: Pipeline) = keyMock.ttl()

  def del()(implicit p: Pipeline) = keyMock.del()

}

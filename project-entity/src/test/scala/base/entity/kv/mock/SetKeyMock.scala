/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 10:59 AM
 */

package base.entity.kv.mock

import base.common.random.RandomService
import base.entity.kv.{ SetKey, Key, IntKey }
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of IntKeyMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SetKeyMock(val token: String = RandomService().md5.toString,
                 membersResult: Future[Set[String]] = Future.successful(Set()),
                 moveResult: Future[Boolean] = Future.successful(true),
                 randResult: Future[Option[String]] = Future.successful(None),
                 randCountResult: Future[Set[String]] = Future.successful(Set()),
                 removeResult: Future[Boolean] = Future.successful(true),
                 addResult: Future[Int] = Future.successful(0),
                 popResult: Future[Option[String]] = Future.successful(None),
                 isMemberResult: Future[Boolean] = Future.successful(true),
                 diffStoreResult: Future[Int] = Future.successful(0),
                 keyMock: Key = new KeyMock()) extends SetKey {

  def members()(implicit p: Pipeline) = membersResult

  def move(to: SetKey, member: Any)(implicit p: Pipeline) = moveResult

  def rand()(implicit p: Pipeline) = randResult

  def rand(count: Int)(implicit p: Pipeline) = randCountResult

  def remove(value: Any)(implicit p: Pipeline) = removeResult

  def add(value: Any*)(implicit p: Pipeline) = addResult

  def pop()(implicit p: Pipeline) = popResult

  def isMember(value: Any)(implicit p: Pipeline) = isMemberResult

  def diffStore(sets: SetKey*)(implicit p: Pipeline) = diffStoreResult

  def exists()(implicit p: Pipeline) = keyMock.exists()

  def expire(seconds: Long)(implicit p: Pipeline) = keyMock.expire(seconds)

  def ttl()(implicit p: Pipeline) = keyMock.ttl()

  def del()(implicit p: Pipeline) = keyMock.del()

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:42 PM
 */

package base.entity.kv.mock

import base.common.random.RandomService
import base.entity.kv.{ KvFactoryService, SetKey, Key, IntKey }
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
                 keyMock: Key = new KeyMock()(KvFactoryService().pipeline))(implicit protected val p: Pipeline)
    extends SetKey {

  def members() = membersResult

  def move(to: SetKey, member: Any) = moveResult

  def rand() = randResult

  def rand(count: Int) = randCountResult

  def remove(value: Any) = removeResult

  def add(value: Any*) = addResult

  def pop() = popResult

  def isMember(value: Any) = isMemberResult

  def diffStore(sets: SetKey*) = diffStoreResult

  def exists() = keyMock.exists()

  def expire(seconds: Long) = keyMock.expire(seconds)

  def ttl() = keyMock.ttl()

  def del() = keyMock.del()

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 1:13 PM
 */

package base.entity.kv

import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SetKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SetKey extends Key {

  def members()(implicit p: Pipeline): Future[Set[String]]

  def isMember(value: Any)(implicit p: Pipeline): Future[Boolean]

  def rand()(implicit p: Pipeline): Future[Option[String]]

  def pop()(implicit p: Pipeline): Future[Option[String]]

  def add(value: Any)(implicit p: Pipeline): Future[Boolean]

  def remove(value: Any)(implicit p: Pipeline): Future[Boolean]

  def move(to: SetKey, member: Any)(implicit p: Pipeline): Future[Boolean]

}

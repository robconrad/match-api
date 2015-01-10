/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 3:28 PM
 */

package base.entity.kv

import base.entity.kv.Key.{ Prop, Pipeline }

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of HashKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait HashKey extends Key {

  def getString(prop: Prop)(implicit p: Pipeline): Future[Option[String]]
  def getInt(prop: Prop)(implicit p: Pipeline): Future[Option[Int]]
  def getLong(prop: Prop)(implicit p: Pipeline): Future[Option[Long]]
  def getFlag(prop: Prop)(implicit p: Pipeline): Future[Boolean]

  def get(props: Array[Prop])(implicit p: Pipeline): Future[Map[Prop, Option[String]]]

  def get(implicit p: Pipeline): Future[Map[Prop, String]]

  def getProps(implicit p: Pipeline): Future[List[Prop]]

  def setFlag(prop: Prop, value: Boolean)(implicit p: Pipeline): Future[Boolean]

  def set(prop: Prop, value: Any)(implicit p: Pipeline): Future[Boolean]

  def set(props: Map[Prop, Any])(implicit p: Pipeline): Future[Boolean]

  def setNx(prop: Prop, value: Any)(implicit p: Pipeline): Future[Boolean]

  def increment(prop: Prop, value: Long)(implicit p: Pipeline): Future[Long]

  def del(prop: Prop)(implicit p: Pipeline): Future[Boolean]

  def del(props: List[Prop])(implicit p: Pipeline): Future[Boolean]

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.kv

import java.util.UUID

import base.entity.kv.Key.{ Prop, Pipeline }
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of HashKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait PrivateHashKey extends Key {

  def getString(prop: Prop): Future[Option[String]]
  def getDateTime(prop: Prop): Future[Option[DateTime]]
  def getId(prop: Prop): Future[Option[UUID]]
  def getInt(prop: Prop): Future[Option[Int]]
  def getLong(prop: Prop): Future[Option[Long]]
  def getFlag(prop: Prop): Future[Boolean]

  def get(props: Array[Prop]): Future[Map[Prop, Option[String]]]

  def get: Future[Map[Prop, String]]

  def getProps: Future[List[Prop]]

  def setFlag(prop: Prop, value: Boolean): Future[Boolean]

  def set(prop: Prop, value: Any): Future[Boolean]

  def set(props: Map[Prop, Any]): Future[Boolean]

  def setNx(prop: Prop, value: Any): Future[Boolean]

  def increment(prop: Prop, value: Long): Future[Long]

  def del(prop: Prop): Future[Boolean]

  def del(props: List[Prop]): Future[Boolean]

}

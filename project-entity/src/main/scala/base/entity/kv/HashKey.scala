/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:49 PM
 */

package base.entity.kv

import java.util.UUID

import base.entity.kv.Key.Prop
import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of ConcreteKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait HashKey extends Key {

  def create(): Future[Boolean]

  def getCreated: Future[Option[DateTime]]
  def getUpdated: Future[Option[DateTime]]

  protected def getString(prop: Prop): Future[Option[String]]
  protected def getDateTime(prop: Prop): Future[Option[DateTime]]
  protected def getId(prop: Prop): Future[Option[UUID]]
  protected def getInt(prop: Prop): Future[Option[Int]]
  protected def getLong(prop: Prop): Future[Option[Long]]
  protected def getFlag(prop: Prop): Future[Boolean]

  protected def get(props: Array[Prop]): Future[Map[Prop, Option[String]]]

  protected def get: Future[Map[Prop, String]]

  protected def getProps: Future[List[Prop]]

  protected def setFlag(prop: Prop, value: Boolean): Future[Boolean]

  protected def set(prop: Prop, value: Any): Future[Boolean]

  protected def set[T <: Map[Prop, Any]](props: T): Future[Boolean]

  protected def setNx(prop: Prop, value: Any): Future[Boolean]

  protected def increment(prop: Prop, value: Long): Future[Long]

  protected def del(prop: Prop): Future[Boolean]

  protected def del(props: List[Prop]): Future[Boolean]

}

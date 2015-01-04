/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 1:15 PM
 */

package base.entity.kv

import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of HashKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait HashKey extends Key {

  def getString(prop: String)(implicit p: Pipeline): Future[Option[String]]
  def getInt(prop: String)(implicit p: Pipeline): Future[Option[Int]]
  def getLong(prop: String)(implicit p: Pipeline): Future[Option[Long]]
  def getFlag(prop: String)(implicit p: Pipeline): Future[Boolean]

  def get(props: Array[String])(implicit p: Pipeline): Future[Map[String, Option[String]]]

  def get(implicit p: Pipeline): Future[Map[String, String]]

  def getKeys(implicit p: Pipeline): Future[List[String]]

  def setFlag(prop: String, value: Boolean)(implicit p: Pipeline): Future[Boolean]

  def set(prop: String, value: Any)(implicit p: Pipeline): Future[Boolean]

  def set(props: Map[String, Any])(implicit p: Pipeline): Future[Boolean]

  def setNx(prop: String, value: Any)(implicit p: Pipeline): Future[Boolean]

  def increment(prop: String, value: Long)(implicit p: Pipeline): Future[Long]

  def del(prop: String)(implicit p: Pipeline): Future[Boolean]

  def del(props: List[String])(implicit p: Pipeline): Future[Boolean]

}

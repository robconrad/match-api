/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 7:06 PM
 */

package base.socket.message

import org.json4s.JsonAST._
import org.json4s.JsonDSL._

/**
 * Implicits to convert various common data types to JValues
 */
trait JsonServerMessageImplicits {

  implicit def mapInt2JValue[A <% JValue](m: Map[Int, A]) =
    JObject(m.toList.map { case (k, v) => JField(k.toString, v) })

  implicit def mapLong2JValue[A <% JValue](m: Map[Long, A]) =
    JObject(m.toList.map { case (k, v) => JField(k.toString, v) })

  implicit def seqMapString2JValue[A <% JValue](l: Traversable[Map[String, A]]) =
    JArray(l.toList.map(m => JObject(m.toList.map { case (k, v) => JField(k.toString, v) })))

  implicit def optionMapString2JValue[A <% JValue](opt: Option[Map[String, A]]): JValue = opt match {
    case Some(x) => x
    case None    => JNothing
  }

  implicit def optionMapStringAny2JValue[A <% JValue](opt: Option[Map[String, Any]]): JValue = opt match {
    case Some(x) => x.map(v => (v._1, v._2.toString))
    case None    => JNothing
  }

}

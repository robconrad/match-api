/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 6:28 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key._
import base.entity.kv._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SetFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SetKeyServiceImpl[T <: SetKey] extends KeyServiceImpl[T] with SetKeyService[T] {

  def remove(sets: List[SetKey], value: Any)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("SREM-MULTI", s"sets: ${sets.map(s => s.token)}, value: $value")
    sets.length > 0 match {
      case true =>
        val futures = sets.map { set =>
          guavaFutureToAkka(p.srem_(set.token, value.asInstanceOf[AnyRef])).map(r => set -> r)
        }
        Future.sequence(futures).map(_.map { case (set, res) => set -> (res.data().intValue() > 0) }).map(_.toMap)
      case false =>
        Future.successful(Map())
    }
  }

  def count(sets: List[SetKey])(implicit p: Pipeline): Future[Map[SetKey, Int]] = {
    if (isDebugEnabled) log("SCARD-MULTI", s"sets: ${sets.map(s => s.token)}")
    sets.length > 0 match {
      case true =>
        val futures = sets.map(set => guavaFutureToAkka(p.scard(set.token)))
        Future.sequence(futures).map { v =>
          sets.zip(v.map(_.data().intValue())).toMap
        }
      case false =>
        Future.successful(Map())
    }
  }

  def unionStore(destination: SetKey, sets: SetKey*)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("SUNIONSTORE", s"destination: $destination, sets: $sets")
    sets.length > 0 match {
      case true  => p.sunionstore(destination.token, sets.map(_.token): _*).map(_.data().intValue())
      case false => Future.successful(0)
    }
  }

}
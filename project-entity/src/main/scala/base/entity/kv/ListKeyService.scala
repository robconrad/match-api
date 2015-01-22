/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:47 AM
 */

package base.entity.kv

import base.entity.kv.Key._
import base.entity.kv.impl.SetKeyImpl

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SetKeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait ListKeyService[A, B <: ListKey[A]] extends KeyService[B] {

  def make(id: Id)(implicit p: Pipeline): B

}

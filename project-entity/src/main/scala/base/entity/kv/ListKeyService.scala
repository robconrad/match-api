/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 9:48 PM
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
trait ListKeyService[T <: ListKey] extends KeyService[T] {

  def make(id: Id): T

}

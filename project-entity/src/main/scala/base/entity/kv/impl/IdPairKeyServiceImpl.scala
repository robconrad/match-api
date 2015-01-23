/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:12 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.entity.kv.Key._
import base.entity.kv.{ IdPair, Key, IdPairKeyService }

/**
 * {{ Describe the high level purpose of IdPairKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IdPairKeyServiceImpl[T <: Key] extends IdPairKeyService[T] {

  def make(a: UUID, b: UUID)(implicit p: Pipeline) = make(IdPair(a, b))

}

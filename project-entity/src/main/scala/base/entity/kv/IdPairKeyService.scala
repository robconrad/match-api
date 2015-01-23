/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:39 PM
 */

package base.entity.kv

import java.util.UUID

import base.entity.kv.Key._

/**
 * {{ Describe the high level purpose of KeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IdPairKeyService[A <: IdPair, B <: Key] extends KeyService[A, B] {

  def make(a: UUID, b: UUID)(implicit p: Pipeline): B

}

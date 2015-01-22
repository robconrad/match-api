/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:36 PM
 */

package base.entity.kv

import base.entity.kv.Key._

/**
 * {{ Describe the high level purpose of IntKeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IntKeyService[A, B <: IntKey] extends KeyService[A, B] {

  def make(id: A)(implicit p: Pipeline): B

}

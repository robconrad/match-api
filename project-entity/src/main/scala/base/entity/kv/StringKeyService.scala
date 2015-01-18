/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:30 PM
 */

package base.entity.kv

import base.entity.kv.Key.{ Pipeline, Id }

/**
 * {{ Describe the high level purpose of IntKeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait StringKeyService[T <: StringKey] extends KeyService[T] {

  def make(id: Id)(implicit p: Pipeline): T

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:24 PM
 */

package base.entity.kv

import base.entity.kv.Key._

/**
 * {{ Describe the high level purpose of SetKeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait ListKeyService[A, B <: ListKey[_]] extends KeyService[A, B] {

  def make(id: A)(implicit p: Pipeline): B

}

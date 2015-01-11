/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:10 PM
 */

package base.entity.kv

import base.entity.kv.Key.Id

/**
 * {{ Describe the high level purpose of IntKeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IntKeyFactory extends KeyFactory {

  final def factoryManifest = manifest[IntKeyFactory]

  def make(id: Id): IntKey

}

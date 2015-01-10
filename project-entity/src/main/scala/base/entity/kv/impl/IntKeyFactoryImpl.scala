/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 3:28 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.Id
import base.entity.kv.{ KeyChannel, IntKeyFactory }

/**
 * {{ Describe the high level purpose of IntFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[kv] final class IntKeyFactoryImpl(protected val keyChannel: KeyChannel)
    extends KeyFactoryImpl with IntKeyFactory {

  def make(id: Id) = new IntKeyImpl(getKey(id), this)

}

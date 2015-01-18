/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.kv.impl

import base.common.lib.Dispatchable
import base.common.time.TimeService
import base.entity.kv.HashKey
import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyProps.CreatedProp

/**
 * {{ Describe the high level purpose of ConcreteKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait HashKeyImpl extends HashKey with Dispatchable {

  def create() = key.setNx(CreatedProp, TimeService().asString())

}

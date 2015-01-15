/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:18 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key.Id
import base.entity.kv.impl.IntKeyServiceImpl
import base.entity.user.kv.{ PhoneCooldownKey, PhoneCooldownKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneCooldownKeyServiceImpl extends IntKeyServiceImpl[PhoneCooldownKey] with PhoneCooldownKeyService {

  def make(id: Id) = new PhoneCooldownKeyImpl(getKey(id), this)

}

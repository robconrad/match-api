/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:55 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.IntKeyServiceImpl
import base.entity.user.kv.{ PhoneCooldownKey, PhoneCooldownKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneCooldownKeyServiceImpl extends IntKeyServiceImpl[String, PhoneCooldownKey] with PhoneCooldownKeyService {

  def make(id: String)(implicit p: Pipeline) = new PhoneCooldownKeyImpl(getKey(id), this)

}

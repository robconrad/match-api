/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:33 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key.{ Pipeline, Id }
import base.entity.kv.impl.{ HashKeyServiceImpl, PrivateHashKeyImpl }
import base.entity.user.kv.{ PhoneKey, PhoneKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneKeyServiceImpl extends HashKeyServiceImpl[PhoneKey] with PhoneKeyService {

  def make(id: Id)(implicit p: Pipeline) = new PhoneKeyImpl(new PrivateHashKeyImpl(getKey(id), this))

}

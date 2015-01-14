/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:09 PM
 */

package base.entity.user.impl

import base.entity.kv.Key.Id
import base.entity.kv.impl.{ PrivateHashKeyImpl, HashKeyServiceImpl }
import base.entity.user.{ PhoneKey, PhoneKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneKeyServiceImpl extends HashKeyServiceImpl[PhoneKey] with PhoneKeyService {

  def make(id: Id) = new PhoneKeyImpl(new PrivateHashKeyImpl(getKey(id), this))

}

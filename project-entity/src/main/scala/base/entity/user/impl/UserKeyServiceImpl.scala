/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:09 PM
 */

package base.entity.user.impl

import base.entity.kv.Key._
import base.entity.kv.impl.{ PrivateHashKeyImpl, HashKeyServiceImpl }
import base.entity.user.{ UserKey, UserKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserKeyServiceImpl extends HashKeyServiceImpl[UserKey] with UserKeyService {

  def make(id: Id) = new UserKeyImpl(new PrivateHashKeyImpl(getKey(id), this))

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:28 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key._
import base.entity.kv.impl.{ HashKeyServiceImpl, PrivateHashKeyImpl }
import base.entity.user.kv.{ UserKey, UserKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserKeyServiceImpl extends HashKeyServiceImpl[UserKey] with UserKeyService {

  def make(id: Id)(implicit p: Pipeline) = new UserKeyImpl(new PrivateHashKeyImpl(getKey(id), this))

}
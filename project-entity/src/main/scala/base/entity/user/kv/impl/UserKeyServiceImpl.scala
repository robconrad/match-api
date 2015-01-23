/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:56 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.entity.kv.Key._
import base.entity.kv.impl.HashKeyServiceImpl
import base.entity.user.kv.{ UserKey, UserKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserKeyServiceImpl
    extends HashKeyServiceImpl[UUID, UserKey]
    with UserKeyService {

  def make(id: UUID)(implicit p: Pipeline) = new UserKeyImpl(getKey(id), this)

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:12 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.{ IdTypedKeyServiceImpl, SetKeyServiceImpl }
import base.entity.user.kv.{ UserGroupsKey, UserGroupsKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserGroupsKeyServiceImpl
    extends SetKeyServiceImpl[UUID, UserGroupsKey]
    with UserGroupsKeyService
    with IdTypedKeyServiceImpl {

  def make(id: UUID)(implicit p: Pipeline) = new UserGroupsKeyImpl(getKey(id), this)

}

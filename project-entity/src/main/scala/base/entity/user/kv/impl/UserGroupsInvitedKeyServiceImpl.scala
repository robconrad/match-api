/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:17 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.SetKeyServiceImpl
import base.entity.user.kv.{ UserGroupsInvitedKey, UserGroupsInvitedKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserGroupsInvitedKeyServiceImpl
    extends SetKeyServiceImpl[UUID, UserGroupsInvitedKey]
    with UserGroupsInvitedKeyService {

  def make(id: UUID)(implicit p: Pipeline) = new UserGroupsInvitedKeyImpl(getKey(id), this)

}

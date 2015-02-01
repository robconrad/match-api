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
import base.entity.user.kv.{ UserPhonesInvitedKey, UserPhonesInvitedKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserPhonesInvitedKeyServiceImpl
    extends SetKeyServiceImpl[UUID, UserPhonesInvitedKey]
    with UserPhonesInvitedKeyService {

  def make(id: UUID)(implicit p: Pipeline) = new UserPhonesInvitedKeyImpl(getKey(id), this)

}

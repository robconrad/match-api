/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 4:49 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{GroupPhonesInvitedKey, GroupPhonesInvitedKeyService}
import base.entity.kv.Key._
import base.entity.kv.impl.SetKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPhonesInvitedKeyServiceImpl
    extends SetKeyServiceImpl[UUID, GroupPhonesInvitedKey]
    with GroupPhonesInvitedKeyService {

  def make(id: UUID)(implicit p: Pipeline) = new GroupPhonesInvitedKeyImpl(getKey(id), this)

}

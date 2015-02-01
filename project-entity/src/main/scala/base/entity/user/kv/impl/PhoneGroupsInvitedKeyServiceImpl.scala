/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:17 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.SetKeyServiceImpl
import base.entity.user.kv.{ PhoneGroupsInvitedKey, PhoneGroupsInvitedKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneGroupsInvitedKeyServiceImpl
    extends SetKeyServiceImpl[String, PhoneGroupsInvitedKey]
    with PhoneGroupsInvitedKeyService {

  def make(id: String)(implicit p: Pipeline) = new PhoneGroupsInvitedKeyImpl(getKey(id), this)

}

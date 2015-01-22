/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:20 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.entity.kv.Key._
import base.entity.kv.{ Key, KeyId }
import base.entity.kv.impl.SimpleKeyServiceImpl
import base.entity.user.kv.{ UserUserLabelKeyService, UserUserLabelKey }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserUserLabelKeyServiceImpl
    extends SimpleKeyServiceImpl[String, UserUserLabelKey] with UserUserLabelKeyService {

  def make(id: Id)(implicit p: Pipeline) = new UserUserLabelKeyImpl(getKey(id), this)

  def make(ownerUserId: UUID, labelUserId: UUID)(implicit p: Pipeline) =
    make(KeyId(ownerUserId + Key.PREFIX_DELIM + labelUserId))

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:42 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.entity.kv.Key._
import base.entity.kv.impl.SimpleKeyServiceImpl
import base.entity.kv.{ IdPair, OrderedIdPair }
import base.entity.user.kv.{ UserUserLabelKey, UserUserLabelKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserUserLabelKeyServiceImpl
    extends SimpleKeyServiceImpl[OrderedIdPair, UserUserLabelKey]
    with UserUserLabelKeyService {

  def make(a: UUID, b: UUID)(implicit p: Pipeline) = make(OrderedIdPair(a, b))

  def make(id: OrderedIdPair)(implicit p: Pipeline) = new UserUserLabelKeyImpl(getKey(id), this)

}

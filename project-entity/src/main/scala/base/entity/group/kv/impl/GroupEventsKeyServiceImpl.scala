/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:46 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupEventsKey, GroupEventsKeyService }
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.ListKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsKeyServiceImpl
    extends ListKeyServiceImpl[UUID, GroupEventsKey]
    with GroupEventsKeyService {

  def make(id: UUID)(implicit p: Pipeline) = new GroupEventsKeyImpl(getKey(id), this)

}

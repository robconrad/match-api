/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:48 AM
 */

package base.entity.group.kv.impl

import base.entity.event.model.EventModel
import base.entity.group.kv.{ GroupEventsKey, GroupEventsKeyService }
import base.entity.kv.Key.{ Id, Pipeline }
import base.entity.kv.impl.ListKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsKeyServiceImpl extends ListKeyServiceImpl[EventModel, GroupEventsKey] with GroupEventsKeyService {

  def make(id: Id)(implicit p: Pipeline) = new GroupEventsKeyImpl(getKey(id), this)

}

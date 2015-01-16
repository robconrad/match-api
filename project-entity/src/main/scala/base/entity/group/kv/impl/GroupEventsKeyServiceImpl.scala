/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:18 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupEventsKeyService, GroupEventsKey }
import base.entity.kv.Key.Id
import base.entity.kv.impl.ListKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsKeyServiceImpl extends ListKeyServiceImpl[GroupEventsKey] with GroupEventsKeyService {

  def make(id: Id) = new GroupEventsKeyImpl(getKey(id), this)

}

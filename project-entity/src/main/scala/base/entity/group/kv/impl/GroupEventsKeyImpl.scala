/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:27 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.group.kv.GroupEventsKey
import base.entity.kv.impl.ListKeyImpl

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsKeyImpl(val keyValue: UUID)
    extends ListKeyImpl[UUID, EventModel]
    with GroupEventsKey

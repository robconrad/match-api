/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 6:15 PM
 */

package base.entity.group.kv

import java.util.UUID

import base.entity.event.model.EventModel
import scredis.keys.ListKey

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupEventsKey extends ListKey[Short, UUID, EventModel]

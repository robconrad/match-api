/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:04 PM
 */

package base.entity.group.kv.impl

import base.entity.event.model.EventModel
import base.entity.group.kv.GroupEventsKey
import base.entity.json.JsonFormats
import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.ListKeyImpl
import org.json4s.native.{ Serialization, JsonMethods }

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsKeyImpl(val token: Array[Byte], protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends ListKeyImpl[EventModel] with GroupEventsKey {

}

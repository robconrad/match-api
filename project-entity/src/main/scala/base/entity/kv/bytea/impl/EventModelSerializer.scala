/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:55 PM
 */

package base.entity.kv.bytea.impl

import base.entity.event.model.EventModel

/**
 * {{ Describe the high level purpose of JsonByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object EventModelSerializer extends JsonScredisSerializer[EventModel] {

  val m = manifest[EventModel]

}

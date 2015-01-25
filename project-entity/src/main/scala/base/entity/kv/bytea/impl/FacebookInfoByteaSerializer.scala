/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 1:20 PM
 */

package base.entity.kv.bytea.impl

import base.entity.facebook.FacebookInfo

/**
 * {{ Describe the high level purpose of JsonByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object FacebookInfoByteaSerializer extends JsonByteaSerializer[FacebookInfo] {

  val m = manifest[FacebookInfo]

}

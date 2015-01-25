/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 1:40 PM
 */

package base.entity.facebook.kv.impl

import base.entity.facebook.kv.{ FacebookInfoKey, FacebookInfoKeyService }
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.HashKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class FacebookInfoKeyServiceImpl
    extends HashKeyServiceImpl[String, FacebookInfoKey]
    with FacebookInfoKeyService {

  def make(id: String)(implicit p: Pipeline) = new FacebookInfoKeyImpl(getKey(id), this)

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/27/15 6:40 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key._
import base.entity.kv.impl.SimpleKeyServiceImpl
import base.entity.user.kv.{ FacebookUserKey, FacebookUserKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class FacebookUserKeyServiceImpl
    extends SimpleKeyServiceImpl[String, FacebookUserKey]
    with FacebookUserKeyService {

  def make(id: String)(implicit p: Pipeline) = new FacebookUserKeyImpl(getKey(id), this)

}

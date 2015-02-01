/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:17 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key._
import base.entity.kv.impl.SimpleKeyServiceImpl
import base.entity.user.kv.{ UserPhone, UserPhoneLabelKey, UserPhoneLabelKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserPhoneLabelKeyServiceImpl
    extends SimpleKeyServiceImpl[UserPhone, UserPhoneLabelKey]
    with UserPhoneLabelKeyService {

  def make(id: UserPhone)(implicit p: Pipeline) = new UserPhoneLabelKeyImpl(getKey(id), this)

}

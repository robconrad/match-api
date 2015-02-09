/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:57 PM
 */

package base.socket.api.test.model

import base.entity.facebook.FacebookService
import base.entity.group.model.InviteModel
import base.entity.group.model.impl.InviteModelImpl

/**
 * {{ Describe the high level purpose of InviteModelFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object InviteModelFactory {

  val label = "bob"

  def apply(phone: String): InviteModel =
    InviteModelImpl(phone, None, Option(label))

  def apply(phone: String, facebookToken: String, name: String): InviteModel =
    InviteModelImpl(phone, Option(FacebookService().getPictureUrl(facebookToken)), Option(name))

}

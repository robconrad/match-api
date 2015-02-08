/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 3:38 PM
 */

package base.socket.api.test.model

import java.util.UUID

import base.entity.facebook.FacebookService
import base.entity.user.model.UserModel

/**
 * {{ Describe the high level purpose of UserModelFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object UserModelFactory {

  def apply(userId: UUID, facebookToken: String, name: String) =
    UserModel(userId, Option(FacebookService().getPictureUrl(facebookToken)), Option(name))

}

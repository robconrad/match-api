/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 3:24 PM
 */

package base.entity.user

import base.entity.kv.KeyProp

/**
 * {{ Describe the high level purpose of UserKeyProps here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[user] object UserKeyProps {

  object UserIdProp extends KeyProp("userId")
  object CodeProp extends KeyProp("code")
  object GenderProp extends KeyProp("gender")
  object NameProp extends KeyProp("name")
  object TokenProp extends KeyProp("token")

}

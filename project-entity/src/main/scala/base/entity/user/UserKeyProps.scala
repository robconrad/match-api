/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 6:55 PM
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

  // device shiz
  object AppVersionProp extends KeyProp("appVersion")
  object LocaleProp extends KeyProp("locale")
  object ModelProp extends KeyProp("model")
  object CordovaProp extends KeyProp("cordova")
  object PlatformProp extends KeyProp("platform")
  object VersionProp extends KeyProp("version")

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 11:30 AM
 */

package base.entity.user.kv

import base.entity.kv.KeyProp

/**
 * {{ Describe the high level purpose of UserKeyProps here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object UserKeyProps {

  object UserIdProp extends KeyProp("userId")
  object FacebookIdProp extends KeyProp("facebookId")
  object CodeProp extends KeyProp("code")
  object GenderProp extends KeyProp("gender")
  object NameProp extends KeyProp("name")
  object TokenProp extends KeyProp("token")
  object PhoneProp extends KeyProp("phone")
  object PhoneCodeProp extends KeyProp("phoneCode")
  object PhoneVerifiedProp extends KeyProp("phoneVerified")

  // user shiz
  object LastLoginProp extends KeyProp("lastLogin")

  // device shiz
  object AppVersionProp extends KeyProp("appVersion")
  object LocaleProp extends KeyProp("locale")
  object ModelProp extends KeyProp("model")
  object CordovaProp extends KeyProp("cordova")
  object PlatformProp extends KeyProp("platform")
  object VersionProp extends KeyProp("version")

}

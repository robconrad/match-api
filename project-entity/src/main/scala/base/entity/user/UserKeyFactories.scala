/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 3:24 PM
 */

package base.entity.user

import base.entity.kv.{ KeyFactoryLocator, IntKeyFactory, HashKeyFactory }

/**
 * {{ Describe the high level purpose of UserKeyFactories here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[user] object UserKeyFactories {

  object DeviceKeyFactory extends KeyFactoryLocator[HashKeyFactory]("device")
  object UserKeyFactory extends KeyFactoryLocator[HashKeyFactory]("user")
  object PhoneKeyFactory extends KeyFactoryLocator[HashKeyFactory]("phone")
  object PhoneCooldownKeyFactory extends KeyFactoryLocator[IntKeyFactory]("phoneCooldown")

}

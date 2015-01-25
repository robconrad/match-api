/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 1:00 PM
 */

package base.entity.facebook.kv

import base.entity.kv.KeyProp

/**
 * {{ Describe the high level purpose of FacebookKeyProps here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object FacebookKeyProps {

  object Id extends KeyProp("id")
  object Username extends KeyProp("username")
  object FirstName extends KeyProp("firstName")
  object Locale extends KeyProp("locale")

}

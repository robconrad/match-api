/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 6:24 PM
 */

package base.entity.facebook.kv

import base.entity.facebook.FacebookInfo
import scredis.keys.SimpleKey

/**
 * {{ Describe the high level purpose of FacebookInfoKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait FacebookInfoKey extends SimpleKey[Short, String, FacebookInfo]

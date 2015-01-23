/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:58 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ SimpleKey, SimpleKeyService }

/**
 * {{ Describe the high level purpose of IntFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SimpleKeyServiceImpl[A, B <: SimpleKey[_]](implicit m: Manifest[A])
    extends KeyServiceImpl[A, B]
    with SimpleKeyService[A, B] {

}

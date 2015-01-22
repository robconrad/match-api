/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:47 AM
 */

package base.entity.kv.impl

import base.entity.kv._

/**
 * {{ Describe the high level purpose of SetFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class ListKeyServiceImpl[A, B <: ListKey[A]] extends KeyServiceImpl[B] with ListKeyService[A, B] {

}

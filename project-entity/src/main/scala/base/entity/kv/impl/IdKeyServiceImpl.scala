/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 4:30 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ IdKeyService, IdKey }

/**
 * {{ Describe the high level purpose of IntFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class IdKeyServiceImpl[T <: IdKey] extends KeyServiceImpl[T] with IdKeyService[T] {

}

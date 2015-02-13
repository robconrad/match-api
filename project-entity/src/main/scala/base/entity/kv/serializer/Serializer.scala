/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:47 PM
 */

package base.entity.kv.serializer

import scredis.serialization.{ Reader, Writer }

/**
 * {{ Describe the high level purpose of ScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait Serializer[T] extends Writer[T] with Reader[T]

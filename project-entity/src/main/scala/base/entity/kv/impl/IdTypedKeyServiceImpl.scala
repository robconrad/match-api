/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:38 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.entity.kv.TypedKeyService

/**
 * {{ Describe the high level purpose of StringTypedKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IdTypedKeyServiceImpl extends TypedKeyService[UUID] {

  protected def toBytes(id: UUID) = UuidUtil.fromUuid(id)
  protected def fromBytes(id: Array[Byte]) = UuidUtil.toUuid(id).get

}

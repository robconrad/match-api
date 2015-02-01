/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 11:04 AM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.bytea.ByteaSerializer
import base.entity.user.kv.UserPhone

/**
 * {{ Describe the high level purpose of StringByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object UserPhoneByteaSerializer extends ByteaSerializer[UserPhone] {

  private val uuidLength = 16

  def serialize(v: UserPhone) = {
    UuidUtil.fromUuid(v.userId) ++ v.phone.getBytes
  }

  def deserialize(v: Array[Byte]) = UserPhone(
    UuidUtil.toUuid(v.slice(0, uuidLength)).get,
    new String(v.slice(uuidLength, v.length)))

}

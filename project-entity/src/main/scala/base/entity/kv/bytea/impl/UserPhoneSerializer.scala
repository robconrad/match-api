/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:56 PM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.bytea.Serializer
import base.entity.user.kv.UserPhone
import scredis.serialization.{ UUIDReader, UUIDWriter }

/**
 * {{ Describe the high level purpose of StringByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object UserPhoneSerializer extends Serializer[UserPhone] {

  private val uuidLength = 16

  def writeImpl(v: UserPhone) = {
    UUIDWriter.write(v.userId) ++ v.phone.getBytes
  }

  def readImpl(v: Array[Byte]) = UserPhone(
    UUIDReader.read(v.slice(0, uuidLength)),
    new String(v.slice(uuidLength, v.length)))

}

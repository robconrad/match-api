/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 11:28 PM
 */

package base.entity.kv.impl

import java.nio.ByteBuffer
import java.util.UUID

import base.common.lib.Tryo

/**
 * {{ Describe the high level purpose of UuidUtil here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off
object UuidUtil {

  def isValid(data: Array[Byte]) = data match {
    case null                      => false
    case data if data.length == 16 => true
    case _                         => false
  }

  // shamelessly stolen from the private UUID ctor
  def toUuid(data: Array[Byte]) = data match {
    case data if !isValid(data) => None
    case data =>
      Tryo {
        var msb = 0L
        var lsb = 0L
        assert(data.length == 16, "data must be 16 bytes in length")
        for (i <- 0 until 8) {
          msb = (msb << 8) | (data(i) & 0xff)
        }
        for (i <- 8 until 16) {
          lsb = (lsb << 8) | (data(i) & 0xff)
        }
        new UUID(msb, lsb)
      }
  }

  def fromUuid(data: UUID) = {
    val bb = ByteBuffer.wrap(new Array[Byte](16))
    bb.putLong(data.getMostSignificantBits)
    bb.putLong(data.getLeastSignificantBits)
    bb.array()
  }

}

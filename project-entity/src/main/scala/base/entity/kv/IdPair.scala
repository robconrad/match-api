/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:47 PM
 */

package base.entity.kv

import java.util.UUID

/**
 * {{ Describe the high level purpose of IdPair here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
sealed trait IdPair {

  def a: UUID
  def b: UUID

}

case class OrderedIdPair(a: UUID, b: UUID) extends IdPair

case class SortedIdPair(unsorted1: UUID, unsorted2: UUID) extends IdPair {

  val (a, b) = unsorted1.compareTo(unsorted2) match {
    case -1 => (unsorted1, unsorted2)
    case 1  => (unsorted2, unsorted1)
    case 0  => throw new RuntimeException("a and b are equal")
  }

}

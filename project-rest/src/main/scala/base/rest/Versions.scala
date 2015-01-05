/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 7:31 PM
 */

package base.rest

/**
 * Representation of API versions
 * @author rconrad
 */
private[rest] object Versions extends Enumeration {
  type Version = Value
  implicit def asString(v: Version) = v.toString

  val V01 = Value(0, "0.1")
  //val V02 = Value(1, "0.2")
  //val V03 = Value(2, "0.3")

  // V03 is default
  val latest = V01

  // versions that the api will respond with as existing and discoverable
  val available = List(latest)

}

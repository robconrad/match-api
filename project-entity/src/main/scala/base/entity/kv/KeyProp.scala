/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 3:16 PM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of KeyProp here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class KeyProp[T](p: T) {
  override lazy val toString = p.toString
}

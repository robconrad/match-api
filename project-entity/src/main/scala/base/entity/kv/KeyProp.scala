/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 3:54 PM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of KeyProp here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class KeyProp[T](p: T) {
  override lazy val toString = {
    val s = p.toString
    KeyProp.register(s, this)
    s
  }
}

object KeyProp {

  private var props = Map[String, KeyProp[_]]()

  private[kv] def clear() {
    props = Map()
  }

  private[kv] def register(prop: String, keyProp: KeyProp[_]) {
    props.contains(prop) match {
      case true  => throw new RuntimeException(s"prop $prop already exists")
      case false => props = props.updated(prop, keyProp)
    }
  }

  def apply(prop: String) = props.get(prop)

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:37 PM
 */

package base.entity.kv

import scala.reflect.Manifest

/**
 * {{ Describe the high level purpose of KeyChannel here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class KeyFactoryLocator[T <: KeyFactory](val prefix: String)(implicit m: Manifest[T]) {

  val factory: T = KeyFactoryLocator(this)

  final def apply() = factory

}

object KeyFactoryLocator {

  private var prefixes = Set[String]()

  private val hash = manifest[HashKeyFactory]
  private val int = manifest[IntKeyFactory]
  private val set = manifest[SetKeyFactory]

  def apply[T <: KeyFactory](ch: KeyFactoryLocator[T])(implicit m: Manifest[T]): T = {
    prefixes.contains(ch.prefix) match {
      case true => throw new RuntimeException(s"prefix '${ch.prefix}' already exists")
      case false =>
        prefixes += ch.prefix
        // TODO lol wtf is this mess
        if (m == hash) {
          KvService().makeHashKeyFactory(ch.asInstanceOf[KeyFactoryLocator[HashKeyFactory]]).asInstanceOf[T]
        } else if (m == int) {
          KvService().makeIntKeyFactory(ch.asInstanceOf[KeyFactoryLocator[IntKeyFactory]]).asInstanceOf[T]
        } else if (m == set) {
          KvService().makeSetKeyFactory(ch.asInstanceOf[KeyFactoryLocator[SetKeyFactory]]).asInstanceOf[T]
        } else {
          throw new RuntimeException(s"unknown manifest $m")
        }
    }
  }

}


/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 4:29 PM
 */

package base.common.random

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import com.google.common.hash.HashCode

import scala.util.Random

/**
 * Provides randomly generated unique identifiers (used to better support mocking)
 * @author rconrad
 */
trait RandomService extends Service {

  final val serviceManifest = manifest[RandomService]

  /**
   * Generates a random int within the bounds specified (min inclusive, max exclusive)
   */
  def int(min: Int = 0, max: Int = Integer.MAX_VALUE): Int

  /**
   * Generate a random md5 hashcode
   */
  def md5: HashCode

  /**
   * Generate a random sha256 hashcode
   */
  def sha256: HashCode

  /**
   * Generate a random uuid
   */
  def uuid: UUID

  /**
   * Get a scala Random impl
   */
  def random: Random

}

object RandomService extends ServiceCompanion[RandomService]

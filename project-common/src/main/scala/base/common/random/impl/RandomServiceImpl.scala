/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 3:58 PM
 */

package base.common.random.impl

import java.security.SecureRandom
import java.util.UUID

import base.common.random.RandomService
import base.common.service.ServiceImpl
import com.google.common.hash.Hashing

import scala.util.Random

/**
 * Provides randomly generated unique identifiers (used to better support mocking)
 * @author rconrad
 */
class RandomServiceImpl extends ServiceImpl with RandomService {

  private val secureRandom = new SecureRandom()

  /**
   * Generates a random int within the bounds specified (min inclusive, max exclusive)
   */
  def int(min: Int, max: Int) = {
    val exclusive = max - min
    secureRandom.nextInt(exclusive) + min
  }

  /**
   * Generate a random md5 hashcode
   */
  def md5 = {
    Hashing.md5().hashLong(secureRandom.nextLong())
  }

  /**
   * Generate a random sha256 hashcode
   */
  def sha256 = {
    Hashing.sha256().hashLong(secureRandom.nextLong())
  }

  /**
   * Generate a random uuid
   */
  def uuid = {
    val u = UUID.randomUUID()
    u
  }

  /**
   * Get a scala Random impl
   */
  lazy val random = new Random(secureRandom)

}

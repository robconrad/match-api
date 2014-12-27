/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.random.impl

import java.security.SecureRandom
import java.util.UUID

import base.common.random.RandomService
import base.common.service.ServiceImpl
import com.google.common.hash.Hashing

/**
 * Provides randomly generated unique identifiers (used to better support mocking)
 * @author rconrad
 */
class RandomServiceImpl extends ServiceImpl with RandomService {

  private val random = new SecureRandom()

  /**
   * Generate a random md5 hashcode
   */
  def md5 = {
    Hashing.md5().hashLong(random.nextLong())
  }

  /**
   * Generate a random sha256 hashcode
   */
  def sha256 = {
    Hashing.sha256().hashLong(random.nextLong())
  }

  /**
   * Generate a random uuid
   */
  def uuid = {
    val u = UUID.randomUUID()
    u
  }

}

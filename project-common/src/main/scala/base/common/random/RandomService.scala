/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.random

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import com.google.common.hash.HashCode

/**
 * Provides randomly generated unique identifiers (used to better support mocking)
 * @author rconrad
 */
trait RandomService extends Service {

  final def serviceManifest = manifest[RandomService]

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

}

object RandomService extends ServiceCompanion[RandomService]

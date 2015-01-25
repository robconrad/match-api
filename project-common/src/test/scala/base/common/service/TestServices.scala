/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 11:51 PM
 */

package base.common.service

/**
 * Wrapper for Services that returns an unregister function for convenient cleanup within a test
 * @author rconrad
 */
object TestServices {

  def register(item: Service*) = {
    val currents = item.map { item =>
      val current = Services.get(item.serviceManifest)
      Services.register(item)
      current
    }
    () => currents.foreach(current => Services.register(current))
  }

}

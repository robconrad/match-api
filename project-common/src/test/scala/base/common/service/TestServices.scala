/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.service

/**
 * Wrapper for Services that returns an unregister function for convenient cleanup within a test
 * @author rconrad
 */
object TestServices {

  def register(item: Service) = {
    val current = Services.get(item.serviceManifest)
    Services.register(item)
    () => Services.register(current)
  }

}

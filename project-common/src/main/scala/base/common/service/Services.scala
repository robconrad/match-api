/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.service

import scala.reflect.Manifest

/**
 * Services Registry. Knows where your services are and lets you inject new ones at any time.
 * @author rconrad
 */
object Services {

  private var map = Map.empty[Manifest[_], Service]

  def apply[T <: Service](implicit m: Manifest[T]): T = {
    map(m).asInstanceOf[T]
  }

  def register(item: Service) {
    map = map.updated(item.serviceManifest, item)
  }

  def get = map

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.service

import scala.reflect.Manifest

/**
 * Base class for all Service companion objects
 *  Makes them into nifty service locators
 * @author rconrad
 */
abstract class ServiceCompanion[T <: Service](implicit m: Manifest[T]) {

  def apply() = Services.apply[T]

}

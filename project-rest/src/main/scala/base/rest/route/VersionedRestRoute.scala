/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.route

import base.rest.Versions._

/**
 * Base class for all /rest/$version/ routes, provides rest-specific directives
 * @author rconrad
 */
private[rest] trait VersionedRestRoute extends RestRoute {

  /**
   * All rest routes must provide a version that they cater to. Typical patterns:
   *
   * 1. Behavior differs between versions:
   *    Different classes will be created for the different versions the properly handle
   *    the varying behavior (e.g. RouteNameV01, RouteNameV02). Often these will
   *    inherit most behavior from a base class.
   *
   * 2. Behavior remains the same between versions:
   *    Often routes will specify this method in an anonymous implementation in a factory
   *    because there is no difference in the behavior of the route between versions.
   */
  def version: Version

}

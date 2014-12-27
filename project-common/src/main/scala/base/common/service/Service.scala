/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:28 PM
 */

package base.common.service

/**
 * Base Services!
 * @author rconrad
 */
trait Service {

  /**
   * Explicit manifest declaration of the Service Interface,
   *  allows Service registration to omit a type parameter
   *  NB: this should always be a final def in the Service Interface
   */
  def serviceManifest: Manifest[_]

}

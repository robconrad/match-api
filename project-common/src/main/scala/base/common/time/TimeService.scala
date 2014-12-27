/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.time

import base.common.service.{ Service, ServiceCompanion }
import org.joda.time.DateTime

/**
 * Provides time functions that can be mocked out
 * @author rconrad
 */
trait TimeService extends Service {

  final def serviceManifest = manifest[TimeService]

  def now: DateTime

}

object TimeService extends ServiceCompanion[TimeService]

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 4:46 PM
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

  def asString(d: DateTime = now): String

  def fromString(s: String): DateTime

}

object TimeService extends ServiceCompanion[TimeService]

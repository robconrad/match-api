/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.service

import base.common.lib.Dispatchable
import base.common.logging.Loggable

/**
 * Base class for all Service impls
 * @author rconrad
 */
trait ServiceImpl extends Service with Loggable with Dispatchable {

  info("initializing - " + this.toString)

}

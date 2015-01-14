/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 7:05 PM
 */

package base.entity.kv.mock

import base.common.logging.Loggable
import base.entity.kv.KeyLogger

/**
 * {{ Describe the high level purpose of KeyLoggerMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object KeyLoggerMock extends KeyLogger with Loggable {

  private[kv] def log(cmd: String, msg: String) {
    log(cmd, "MOCK", msg)
  }

  private[kv] def log(cmd: String, token: String, msg: String = "") {
    debug(s"Redis.$cmd:: token: $token, $msg")
  }

}

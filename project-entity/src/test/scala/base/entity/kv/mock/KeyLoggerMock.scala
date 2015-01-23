/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:16 PM
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

  def log(cmd: String, msg: String) {
    log(cmd, "MOCK".getBytes, msg)
  }

  def log(cmd: String, token: Array[Byte], msg: String = "") {
    debug(s"Redis.$cmd:: token: ${new String(token)}, $msg")
  }

}

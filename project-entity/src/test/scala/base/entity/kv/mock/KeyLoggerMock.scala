/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 11:54 AM
 */

package base.entity.kv.mock

import base.entity.kv.KeyLogger

/**
 * {{ Describe the high level purpose of KeyLoggerMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object KeyLoggerMock extends KeyLogger {

  private[kv] def log(cmd: String, msg: String) {}

  private[kv] def log(cmd: String, token: String, msg: String) {}

}

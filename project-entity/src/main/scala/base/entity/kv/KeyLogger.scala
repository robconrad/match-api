/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 11:41 AM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of KeyLogger here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait KeyLogger {

  private[kv] def log(cmd: String, msg: String)

  private[kv] def log(cmd: String, token: String, msg: String = "")

}

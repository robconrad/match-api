/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:10 PM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of KeyLogger here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait KeyLogger {

  def log(cmd: String, msg: String)

  def log(cmd: String, token: String, msg: String = "")

}

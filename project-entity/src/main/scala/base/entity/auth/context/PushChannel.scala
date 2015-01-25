/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:06 AM
 */

package base.entity.auth.context

import base.entity.command.model.CommandModel

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of PushChannel here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait PushChannel {

  def push[T <: CommandModel[_]](command: T)(implicit m: Manifest[T]): Future[Boolean]

}

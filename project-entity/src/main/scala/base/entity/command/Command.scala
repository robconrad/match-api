/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:58 PM
 */

package base.entity.command

import base.entity.auth.context.ChannelContext
import base.entity.service.CrudImplicits

/**
 * {{ Describe the high level purpose of Command here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] abstract class Command[A, B] extends CrudImplicits[B] {

  implicit def channelCtx: ChannelContext

  def input: A

  def execute(): Response

}

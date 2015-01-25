/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:44 PM
 */

package base.entity.command

import base.entity.auth.context.ChannelContext
import base.entity.kv.KvFactoryService
import base.entity.service.CrudImplicits

/**
 * {{ Describe the high level purpose of Command here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] abstract class Command[A, B] extends CrudImplicits[B] {

  protected implicit lazy val p = KvFactoryService().pipeline

  implicit def channelCtx: ChannelContext

  def input: A

  def execute(): Response

}

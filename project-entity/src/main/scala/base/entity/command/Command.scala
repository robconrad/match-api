/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:27 AM
 */

package base.entity.command

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

  def input: A

  def execute(): Response

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 7:57 PM
 */

package base.entity.command.impl

import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.CommandService
import base.entity.command.model.CommandInputModel
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest

/**
 * {{ Describe the high level purpose of CommandServiceTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class CommandServiceImplTest[T <: CommandInputModel] extends EntityServiceTest with KvTest {

  def service: CommandService[T, _]

  def model: T

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("without group membership") {
    implicit val channelCtx = ChannelContextDataFactory.godPerms
    model.assertGroupId.isDefined match {
      case true  => assert(service.execute(model).await() == CommandServiceImpl.groupPermError.await())
      case false => assert(service.execute(model).await() != CommandServiceImpl.groupPermError.await())
    }
  }

}

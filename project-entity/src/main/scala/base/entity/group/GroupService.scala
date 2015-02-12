/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.ChannelContext
import base.entity.error.model.ApiError
import base.entity.group.GroupService.GetGroup
import base.entity.group.model.GroupModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait GroupService extends Service {

  final val serviceManifest = manifest[GroupService]

  def getGroup(userId: UUID, groupId: UUID)(implicit channelCtx: ChannelContext): GetGroup

}

object GroupService extends ServiceCompanion[GroupService] {

  type GetGroup = Future[Either[ApiError, Option[GroupModel]]]

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.{ ChannelContext, AuthContext }
import base.entity.error.model.ApiError
import base.entity.group.GroupService.GetGroup
import base.entity.group.model.GroupModel
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait GroupService extends Service {

  final val serviceManifest = manifest[GroupService]

  def getGroup(userId: UUID, groupId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext): GetGroup

}

object GroupService extends ServiceCompanion[GroupService] {

  type GetGroup = Future[Either[ApiError, Option[GroupModel]]]

}

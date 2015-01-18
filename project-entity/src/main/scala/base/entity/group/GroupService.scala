/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:58 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
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

  def getGroup(groupId: UUID)(implicit p: Pipeline, authCtx: AuthContext): GetGroup

}

object GroupService extends ServiceCompanion[GroupService] {

  type GetGroup = Future[Either[ApiError, Option[GroupModel]]]

}

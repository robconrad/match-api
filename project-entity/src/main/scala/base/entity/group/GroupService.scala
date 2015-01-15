/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:10 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.group.model.GroupModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait GroupService extends Service {

  final def serviceManifest = manifest[GroupService]

  def getGroups(userId: UUID)(implicit p: Pipeline): Future[Either[ApiError, List[GroupModel]]]

}

object GroupService extends ServiceCompanion[GroupService] {

}

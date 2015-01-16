/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 4:07 PM
 */

package base.entity.group.mock

import java.util.UUID

import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.group.GroupService
import base.entity.group.model.GroupModel

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupServiceMock(getGroupResult: Future[Either[ApiError, Option[GroupModel]]] = Future.successful(Right(None)),
                       getGroupsResult: Future[Either[ApiError, List[GroupModel]]] = Future.successful(Right(List())))
    extends GroupService {

  def getGroup(groupId: UUID)(implicit p: Pipeline) = getGroupResult

  def getGroups(userId: UUID)(implicit p: Pipeline) = getGroupsResult

}

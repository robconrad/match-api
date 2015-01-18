/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 3:09 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.group.GroupService
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupServiceImpk here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupServiceImpl extends ServiceImpl with GroupService {

  def getGroup(groupId: UUID)(implicit p: Pipeline) =
    Future.successful(Right(None))

  def getGroups(userId: UUID)(implicit p: Pipeline) =
    Future.successful(Right(List()))

}

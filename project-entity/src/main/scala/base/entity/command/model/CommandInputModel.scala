/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 7:12 PM
 */

package base.entity.command.model

import java.util.UUID

import base.entity.model.Model

/**
 * {{ Describe the high level purpose of GroupCommand here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait CommandInputModel extends Model {

  def assertGroupId: Option[UUID]

}

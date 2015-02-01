/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 3:00 PM
 */

package base.entity.group.model.impl

import java.util.UUID

import base.entity.group.model.InviteModel

/**
 * API describable model
 * @author rconrad
 */
case class InviteModelImpl(
  groupId: UUID,
  phone: Option[String],
  pictureUrl: Option[String],
  name: String)
    extends InviteModel

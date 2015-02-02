/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 5:48 PM
 */

package base.entity.group.model.impl

import base.entity.group.model.InviteModel

/**
 * API describable model
 * @author rconrad
 */
case class InviteModelImpl(
  phone: String,
  pictureUrl: Option[String],
  name: Option[String])
    extends InviteModel

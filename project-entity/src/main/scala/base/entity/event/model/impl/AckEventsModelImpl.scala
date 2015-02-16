/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:46 PM
 */

package base.entity.event.model.impl

import java.util.UUID

import base.entity.event.model.AckEventsModel

/**
 * API describable model
 * @author rconrad
 */
case class AckEventsModelImpl(groupId: UUID) extends AckEventsModel

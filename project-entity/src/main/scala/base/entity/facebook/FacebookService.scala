/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 1:40 PM
 */

package base.entity.facebook

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.ChannelContext

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
 * CRUD, etc.
 * @author rconrad
 */
trait FacebookService extends Service {

  final val serviceManifest = manifest[FacebookService]

  def getInfo(token: String)(implicit channelCtx: ChannelContext): Future[Option[FacebookInfo]]

}

object FacebookService extends ServiceCompanion[FacebookService]

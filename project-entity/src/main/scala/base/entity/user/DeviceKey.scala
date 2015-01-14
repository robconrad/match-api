/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:10 PM
 */

package base.entity.user

import java.util.UUID

import base.entity.error.ApiError
import base.entity.kv.{ HashKey, PrivateHashKey }
import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait DeviceKey extends HashKey {

  def getToken(implicit p: Pipeline): Future[Option[UUID]]

  def getUserId(implicit p: Pipeline): Future[Option[UUID]]

  def setTokenAndUserId(token: UUID, userId: UUID)(implicit p: Pipeline): Future[Boolean]

  def set(appVersion: String,
          locale: String,
          model: String,
          cordova: String,
          platform: String,
          version: String)(implicit p: Pipeline): Future[Boolean]

}

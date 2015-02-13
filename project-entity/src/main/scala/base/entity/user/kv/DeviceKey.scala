/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 7:39 PM
 */

package base.entity.user.kv

import java.util.UUID

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait DeviceKey {

  def getToken: Future[Option[UUID]]
  def setToken(token: UUID): Future[Boolean]

  def getUserId: Future[Option[UUID]]
  def setUserId(userId: UUID): Future[Boolean]

  def setTokenAndUserId(token: UUID, userId: UUID): Future[Unit]

  def set(appVersion: String,
          locale: String,
          model: Option[String],
          cordova: Option[String],
          platform: Option[String],
          version: Option[String]): Future[Unit]

  def getAppVersion: Future[Option[String]]
  def getLocale: Future[Option[String]]
  def getModel: Future[Option[String]]
  def getCordova: Future[Option[String]]
  def getPlatform: Future[Option[String]]
  def getVersion: Future[Option[String]]

}

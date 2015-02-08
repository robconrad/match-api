/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 11:45 AM
 */

package base.socket.api.test

import java.util.UUID

import base.common.test.TestExceptions.TestRuntimeException

/**
 * {{ Describe the high level purpose of SocketProperties here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SocketProperties {

  implicit class PimpMyOptions[T](o: Option[T]) {
    def getOrThrow = o.getOrElse(throw new TestRuntimeException(s"$o not set"))
  }

  private var _deviceId: Option[UUID] = None
  def deviceId_=(deviceId: UUID) { _deviceId = Option(deviceId) }
  def deviceId = _deviceId.getOrThrow

  private var _facebookToken: Option[String] = None
  def facebookToken_=(facebookToken: String) { _facebookToken = Option(facebookToken) }
  def facebookToken = _facebookToken.getOrThrow

  private var _userId: Option[UUID] = None
  def userId_=(userId: UUID) { _userId = Option(userId) }
  def userId = _userId.getOrThrow

  private var _name: Option[String] = None
  def name_=(name: String) { _name = Option(name) }
  def name = _name.getOrThrow

  private var _phone: Option[String] = None
  def phone_=(phone: String) { _phone = Option(phone) }
  def phone = _phone.getOrThrow

}

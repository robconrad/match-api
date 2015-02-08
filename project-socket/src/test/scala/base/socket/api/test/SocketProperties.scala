/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 12:54 PM
 */

package base.socket.api.test

import java.util.UUID

import base.common.random.RandomService
import base.common.test.TestExceptions.TestRuntimeException
import base.entity.facebook.FacebookService
import base.entity.group.model.impl.InviteModelImpl
import base.entity.user.model.UserModel

/**
 * {{ Describe the high level purpose of SocketProperties here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SocketProperties(
  private var _deviceId: Option[UUID] = Option(RandomService().uuid),
  private var _facebookToken: Option[String] = Option(RandomService().uuid.toString),
  private var _userId: Option[UUID] = None,
  private var _name: Option[String] = Option("name-" + RandomService().md5),
  private var _phone: Option[String] = Option("phone-" + RandomService().md5)) {

  implicit class PimpMyOptions[T](o: Option[T]) {
    def getOrThrow = o.getOrElse(throw new TestRuntimeException(s"$o not set"))
  }

  def deviceId_=(deviceId: UUID) { _deviceId = Option(deviceId) }
  def deviceId = _deviceId.getOrThrow

  def facebookToken_=(facebookToken: String) { _facebookToken = Option(facebookToken) }
  def facebookToken = _facebookToken.getOrThrow

  def userId_=(userId: UUID) { _userId = Option(userId) }
  def userId = _userId.getOrThrow

  def name_=(name: String) { _name = Option(name) }
  def name = _name.getOrThrow

  def phone_=(phone: String) { _phone = Option(phone) }
  def phone = _phone.getOrThrow


  def userModel = UserModel(userId, Option(FacebookService().getPictureUrl(facebookToken)), Option(name))
  def inviteModel = InviteModelImpl(phone, Option(FacebookService().getPictureUrl(facebookToken)), Option(name))

}

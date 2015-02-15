/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 11:49 AM
 */

package base.socket.api.test

import java.util.UUID

import base.common.random.RandomService
import base.common.test.TestExceptions.TestRuntimeException
import base.entity.question.QuestionSides.QuestionSide
import base.socket.api.test.model.{ InviteModelFactory, UserModelFactory }
import org.joda.time.DateTime

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
    private var _phone: Option[String] = None,
    private var _lastLogin: Option[DateTime] = None,
    private var _questionsAnswered: Map[UUID, List[(Int, QuestionSide)]] = Map(),
    private var _groups: List[TestGroup] = List(),
    private var _pendingGroups: List[TestGroup] = List()) {

  val phoneString = "phone-" + RandomService().md5

  implicit class PimpMyOptions[T](o: Option[T]) {
    def getOrThrow = o.getOrElse(throw new TestRuntimeException(s"$o not set"))
  }

  def deviceId_=(deviceId: UUID) { _deviceId = Option(deviceId) }
  def deviceId = _deviceId.getOrThrow

  def facebookToken_=(facebookToken: String) { _facebookToken = Option(facebookToken) }
  def facebookToken = _facebookToken.getOrThrow

  def userId_=(userId: UUID) { _userId = Option(userId) }
  def userId = _userId.getOrThrow
  def userIdOpt = _userId

  def name_=(name: String) { _name = Option(name) }
  def name = _name.getOrThrow

  def setPhone() { _phone = Option(phoneString) }
  def phone = _phone.getOrThrow
  def phoneOpt = _phone

  def lastLogin_=(lastLogin: DateTime) { _lastLogin = Option(lastLogin) }
  def lastLogin = _lastLogin

  def answerQuestion(groupId: UUID, questionIndex: Int, questionSide: QuestionSide) {
    val qa = _questionsAnswered.getOrElse(groupId, List()) ++ List((questionIndex, questionSide))
    _questionsAnswered += (groupId -> qa)
  }
  def questionsAnswered(groupId: UUID) = _questionsAnswered.getOrElse(groupId, List())

  def groups_=(groups: List[TestGroup]) { _groups = groups }
  def groups = _groups

  def pendingGroups_=(pendingGroups: List[TestGroup]) { _pendingGroups = pendingGroups }
  def pendingGroups = _pendingGroups

  def userModel = UserModelFactory(userId, facebookToken, name)
  def inviteModel = InviteModelFactory(phoneString, facebookToken, name)

}

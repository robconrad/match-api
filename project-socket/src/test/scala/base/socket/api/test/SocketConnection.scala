/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 9:15 PM
 */

package base.socket.api.test

import base.socket.api.test.command._

/**
 * {{ Describe the high level purpose of SocketConnection here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SocketConnection(var _props: SocketProperties) {

  implicit val self = this

  lazy val login = new LoginCommandHandler
  lazy val register = new RegisterCommandHandler
  lazy val verify = new VerifyCommandHandler
  lazy val sendInvite = new SendInviteCommandHandler
  lazy val acceptInvite = new AcceptInviteCommandHandler
  lazy val declineInvite = new DeclineInviteCommandHandler
  lazy val questions = new QuestionsCommandHandler
  lazy val message = new MessageCommandHandler
  lazy val answer = new AnswerCommandHandler
  lazy val createQuestion = new CreateQuestionCommandHandler

  def props_=(props: SocketProperties) { _props = props }
  def props = _props

  def connect(): SocketConnection

  protected def _disconnect(): SocketConnection
  final def disconnect() = {
    props.groups.foreach { group =>
      group.sockets = group.sockets.filter(_ != this)
    }
    _disconnect()
  }

  def read: String

  def write(json: String)

  def isActive: Boolean

}

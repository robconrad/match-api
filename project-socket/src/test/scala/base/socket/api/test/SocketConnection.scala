/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 12:41 PM
 */

package base.socket.api.test

import base.socket.api.test.command._

/**
 * {{ Describe the high level purpose of SocketConnection here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SocketConnection {

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

  val props: SocketProperties

  def disconnect()

  def read: String

  def write(json: String)

  def isActive: Boolean

}

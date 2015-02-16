/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.command

import base.entity.error.model.ApiError
import base.entity.event.model.{ AckEventsModel, EventModel }
import base.entity.group.model._
import base.entity.message.model.MessageModel
import base.entity.question.model._
import base.entity.user.model._

/**
 * {{ Describe the high level purpose of Commands here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object CommandNames extends Enumeration {
  type CommandName = Value

  val error = Value

  val heartbeat = Value

  val login = Value
  val loginResponse = Value

  val registerPhone = Value
  val registerPhoneResponse = Value

  val verifyPhone = Value
  val verifyPhoneResponse = Value

  val sendInvite = Value
  val sendInviteResponse = Value

  val acceptInvite = Value
  val acceptInviteResponse = Value

  val declineInvite = Value
  val declineInviteResponse = Value

  val questions = Value
  val questionsResponse = Value

  val createQuestion = Value
  val createQuestionResponse = Value

  val ackEvents = Value

  val message = Value
  val answer = Value
  val event = Value

  def findByName(name: String) = names.get(name)
  def findByType[T](implicit m: Manifest[T]) = typeValue.get(m)

  private val names = values.map(v => v.toString -> v).toMap

  private val typeValue = Map[Any, Value](
    manifest[ApiError] -> error,
    manifest[LoginModel] -> login,
    manifest[LoginResponseModel] -> loginResponse,
    manifest[RegisterPhoneModel] -> registerPhone,
    manifest[RegisterPhoneResponseModel] -> registerPhoneResponse,
    manifest[VerifyPhoneModel] -> verifyPhone,
    manifest[VerifyPhoneResponseModel] -> verifyPhoneResponse,
    manifest[SendInviteModel] -> sendInvite,
    manifest[SendInviteResponseModel] -> sendInviteResponse,
    manifest[AcceptInviteModel] -> acceptInvite,
    manifest[AcceptInviteResponseModel] -> acceptInviteResponse,
    manifest[DeclineInviteModel] -> declineInvite,
    manifest[DeclineInviteResponseModel] -> declineInviteResponse,
    manifest[QuestionsModel] -> questions,
    manifest[QuestionsResponseModel] -> questionsResponse,
    manifest[CreateQuestionModel] -> createQuestion,
    manifest[CreateQuestionResponseModel] -> createQuestionResponse,
    manifest[AckEventsModel] -> ackEvents,
    manifest[MessageModel] -> message,
    manifest[AnswerModel] -> answer,
    manifest[EventModel] -> event)

}

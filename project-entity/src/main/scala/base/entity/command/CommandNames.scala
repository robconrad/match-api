/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:30 AM
 */

package base.entity.command

import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.message.model.MessageModel
import base.entity.question.model.{ AnswerModel, QuestionsModel, QuestionsResponseModel }
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

  val invite = Value
  val inviteResponse = Value

  val questions = Value
  val questionsResponse = Value

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
    manifest[InviteModel] -> invite,
    manifest[InviteResponseModel] -> inviteResponse,
    manifest[QuestionsModel] -> questions,
    manifest[QuestionsResponseModel] -> questionsResponse,
    manifest[MessageModel] -> message,
    manifest[AnswerModel] -> answer,
    manifest[EventModel] -> event)

}

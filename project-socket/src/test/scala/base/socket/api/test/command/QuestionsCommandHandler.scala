/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 11:52 AM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.question.model.{QuestionModel, QuestionsModel, QuestionsResponseModel}
import base.socket.api.test.ListUtils._
import base.socket.api.test.SocketConnection

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionsCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(groupId: UUID, questionModels: List[QuestionModel])(implicit executor: CommandExecutor) {
    val questionsModel = QuestionsModel(groupId)
    val questionsResponseModel = QuestionsResponseModel(groupId, sortQuestions(questionModels))
    executor(questionsModel, Option(questionsResponseModel))
  }

}

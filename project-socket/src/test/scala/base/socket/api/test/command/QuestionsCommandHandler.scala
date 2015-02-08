/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 2:16 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.question.model.{QuestionsModel, QuestionsResponseModel}
import base.socket.api.test.SocketConnection
import base.socket.api.test.util.TestQuestions

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionsCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(groupId: UUID, filteredQuestions: List[Int] = List())
           (implicit executor: CommandExecutor, questions: TestQuestions) {
    val questionsModel = QuestionsModel(groupId)
    val questionsResponseModel = QuestionsResponseModel(groupId, questions.filteredModels(filteredQuestions))
    executor(questionsModel, Option(questionsResponseModel))
  }

}

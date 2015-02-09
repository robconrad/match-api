/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:57 PM
 */

package base.socket.api.test.command

import base.entity.question.model.{ QuestionsModel, QuestionsResponseModel }
import base.socket.api._
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionsCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup)(implicit executor: CommandExecutor, questions: TestQuestions) {
    val questionsModel = QuestionsModel(group.id)
    val questionsResponseModel = QuestionsResponseModel(group.id,
      questions.filteredModels(socket.questionsAnswered(group.id)))
    executor(questionsModel, Option(questionsResponseModel))
  }

}

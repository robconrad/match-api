/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 4:59 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.entity.question.QuestionDef
import base.entity.question.model._
import base.socket.api.test.command.CreateQuestionCommandHandler._
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CreateQuestionCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup)(implicit executor: CommandExecutor,
                              randomMock: RandomServiceMock, testQuestions: TestQuestions) {
    testQuestions.addGroupDef(group.id, QuestionDef(randomMock.nextUuid(), sideA, Option(sideB)))
    val createQuestionModel = CreateQuestionModel(group.id, sideA, Option(sideB))
    val responseModel = CreateQuestionResponseModel(group.id, randomMock.nextUuid())
    executor(createQuestionModel, Option(responseModel))
  }

}

object CreateQuestionCommandHandler {

  val sideA = "user question side a"
  val sideB = "user question side b"

}

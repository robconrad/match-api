/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 9:22 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.entity.question.model._
import base.socket.api.test.command.CreateQuestionCommandHandler._
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CreateQuestionCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup)(implicit executor: CommandExecutor, randomMock: RandomServiceMock) {
    val createQuestionModel = CreateQuestionModel(group.id, sideA, Option(sideB))
    val responseModel = CreateQuestionResponseModel(group.id, randomMock.nextUuid())
    executor(createQuestionModel, Option(responseModel))
  }

}

object CreateQuestionCommandHandler {

  val sideA = "question side a"
  val sideB = "question side b"

}

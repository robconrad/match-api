/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 4:20 PM
 */

package base.socket.api.test.command

import base.entity.user.model._
import base.socket.api._
import base.socket.api.test.{SocketConnection, TestGroup}

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class VerifyCommandHandler(implicit s: SocketConnection) extends CommandHandler {

  def apply(pendingGroups: List[TestGroup] = List())(implicit executor: CommandExecutor) {
    val code = "code!"
    val verifyModel = VerifyPhoneModel(s.phone, code)
    val verifyResponseModel = VerifyPhoneResponseModel(s.phone, pendingGroups.map(_.model))
    executor(verifyModel, Option(verifyResponseModel))
  }

}

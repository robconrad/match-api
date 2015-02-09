/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 4:13 PM
 */

package base.socket.api.test.command

import base.entity.group.model.GroupModel
import base.entity.user.model._
import base.socket.api.test.SocketConnection
import base.socket.api._

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class VerifyCommandHandler(implicit s: SocketConnection) extends CommandHandler {

  def apply(pendingGroups: List[GroupModel] = List())(implicit executor: CommandExecutor) {
    val code = "code!"
    val verifyModel = VerifyPhoneModel(s.phone, code)
    val verifyResponseModel = VerifyPhoneResponseModel(s.phone, pendingGroups)
    executor(verifyModel, Option(verifyResponseModel))
  }

}

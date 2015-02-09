/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 4:15 PM
 */

package base.socket.api.test.command

import base.entity.user.model._
import base.socket.api.test.SocketConnection
import base.socket.api._

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class RegisterCommandHandler(implicit s: SocketConnection) extends CommandHandler {

  def apply()(implicit executor: CommandExecutor) {
    val registerModel = RegisterPhoneModel(s.phone)
    val registerResponseModel = RegisterPhoneResponseModel(s.phone)
    executor(registerModel, Option(registerResponseModel))
  }

}

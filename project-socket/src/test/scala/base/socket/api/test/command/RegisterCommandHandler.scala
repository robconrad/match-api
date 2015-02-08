/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 11:45 AM
 */

package base.socket.api.test.command

import base.entity.user.model._
import base.socket.api.test.SocketConnection

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class RegisterCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  import socket.props._

  def apply()(implicit executor: CommandExecutor) {
    val registerModel = RegisterPhoneModel(phone)
    val registerResponseModel = RegisterPhoneResponseModel(phone)
    executor(registerModel, Option(registerResponseModel))
  }

}

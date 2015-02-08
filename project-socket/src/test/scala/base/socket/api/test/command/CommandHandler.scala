/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 3:55 PM
 */

package base.socket.api.test.command

import base.common.test.PimpMyFutures
import base.socket.api.test.{SocketProperties, SocketConnection}

/**
 * {{ Describe the high level purpose of CommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait CommandHandler extends PimpMyFutures {

  implicit def socket2Props(s: SocketConnection): SocketProperties = s.props

}

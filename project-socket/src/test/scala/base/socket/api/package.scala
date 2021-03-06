/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.socket

import base.socket.api.test.{ SocketConnection, SocketProperties }

/**
 * {{ Describe the high level purpose of package here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
package object api {

  implicit def socket2Props(s: SocketConnection): SocketProperties = s.props

}

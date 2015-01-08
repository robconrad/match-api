/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/7/15 9:54 PM
 */

package base.socket.api

import base.common.lib.Dispatchable
import base.socket.logging.SocketLoggable

object SocketApiStats extends Enumeration with SocketLoggable with Dispatchable {
  type SocketApiStat = Value

  val CONNECTIONS = Value
  val SESSIONS = Value

  implicit class Props(stat: SocketApiStat) {
    def isCumulative = stat match {
      case CONNECTIONS | SESSIONS => false
    }
  }

}

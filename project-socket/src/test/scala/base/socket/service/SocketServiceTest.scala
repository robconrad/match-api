/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 12:18 PM
 */

package base.socket.service

import base.common.lib.Dispatchable
import base.common.logging.Loggable
import base.common.service.{ ServiceTest, ServicesBeforeAndAfterAll }
import base.socket.test.SocketBaseSuite

/**
 * Base service test class, sets up other services etc.
 * @author rconrad
 */
private[socket] abstract class SocketServiceTest
    extends ServiceTest
    with SocketBaseSuite
    with ServicesBeforeAndAfterAll
    with Dispatchable
    with Loggable {

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:28 PM
 */

package base.socket.api.test

import base.socket.api.SocketApiHandlerService
import base.socket.test.SocketBaseSuite

/**
 * {{ Describe the high level purpose of IntegrationSuite here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[api] trait IntegrationSuite extends SocketBaseSuite {

  def handlerService: SocketApiHandlerService

  def makeGroup() = new TestGroup()
  def makeSocket(): SocketConnection = makeSocket(new SocketProperties())
  def makeSocket(props: SocketProperties): SocketConnection

}

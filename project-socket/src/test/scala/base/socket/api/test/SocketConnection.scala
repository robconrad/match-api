/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 9:19 PM
 */

package base.socket.api.test

/**
 * {{ Describe the high level purpose of SocketConnection here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SocketConnection {

  def disconnect()

  def read: String

  def write(json: String)

}

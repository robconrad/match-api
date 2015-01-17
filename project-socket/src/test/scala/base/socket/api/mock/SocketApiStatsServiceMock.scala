/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 1:22 PM
 */

package base.socket.api.mock

import base.socket.api.SocketApiStats.SocketApiStat
import base.socket.api.SocketApiStatsService

/**
 * {{ Describe the high level purpose of SocketApiStatsServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SocketApiStatsServiceMock(getResult: Long = 0L) extends SocketApiStatsService {

  def increment(stat: SocketApiStat) {}

  def get(stat: SocketApiStat) = getResult

  def decrement(stat: SocketApiStat) {}

  def report() {}

  def add(stat: SocketApiStat, delta: Long) {}

  def getStats(stat: SocketApiStat) = Map()

}

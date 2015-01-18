/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:58 PM
 */

package base.socket.api

import base.common.service.{ Service, ServiceCompanion }
import base.socket.api.SocketApiStats.SocketApiStat
import base.socket.api.SocketApiStatsService.SocketApiStats

/**
 * Responsible for maintaining statistics on the API since it has been up
 * @author rconrad
 */
trait SocketApiStatsService extends Service {

  final val serviceManifest = manifest[SocketApiStatsService]

  def increment(stat: SocketApiStat)

  def decrement(stat: SocketApiStat)

  def add(stat: SocketApiStat, delta: Long)

  def get(stat: SocketApiStat): Long

  def getStats(stat: SocketApiStat): SocketApiStats

  def report()

}

object SocketApiStatsService extends ServiceCompanion[SocketApiStatsService] {

  type SocketApiStats = Map[String, List[Long]]

}

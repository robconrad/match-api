/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 11:35 AM
 */

package base.socket.api.impl

import java.util.concurrent.atomic.AtomicLong

import base.common.lib.Actors
import base.common.service.ServiceImpl
import base.socket.api.SocketApiStats._
import base.socket.api.SocketApiStatsService

import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of SocketApiStatsServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SocketApiStatsServiceImpl(interval: Option[FiniteDuration]) extends ServiceImpl with SocketApiStatsService {
  type SocketApiStatsMap = Map[SocketApiStat, AtomicLong]

  private val scheduler = Actors.actorSystem.scheduler

  private val NAME_TOTAL = "total"
  private val NAME_MINUTES = "minutely"
  private val NAME_HOURS = "hourly"
  private val NAME_DAYS = "daily"

  private val MINUTES = 60
  private val HOURS = 24
  private val DAYS = 30

  private val stats = makeStatsMap()
  private val minutelyStats = List.fill(MINUTES)(makeStatsMap())
  private val hourlyStats = List.fill(HOURS)(makeStatsMap())
  private val dailyStats = List.fill(DAYS)(makeStatsMap())

  private var minute = 0
  private var hour = 0
  private var day = 0

  interval.map { interval =>
    scheduler.schedule(0.seconds, interval) {
      report()
    }
    scheduler.schedule(1.minute, 1.minute) {
      values.foreach(setToTotal(_, minutelyStats(minute)))
      minute = (minute + 1) % MINUTES
      values.foreach(reset(_, minutelyStats(minute)))
    }
    scheduler.schedule(1.hour, 1.hour) {
      values.foreach(setToTotal(_, hourlyStats(hour)))
      hour = (hour + 1) % HOURS
      values.foreach(reset(_, hourlyStats(hour)))
    }
    scheduler.schedule(1.day, 1.day) {
      values.foreach(setToTotal(_, dailyStats(day)))
      day = (day + 1) % DAYS
      values.foreach(reset(_, dailyStats(day)))
    }
  }

  def increment(stat: SocketApiStat) {
    stats(stat).incrementAndGet()
    if (stat.isCumulative) {
      foreach(stat, _.incrementAndGet())
    }
  }

  def decrement(stat: SocketApiStat) {
    stats(stat).decrementAndGet()
    if (stat.isCumulative) {
      foreach(stat, _.decrementAndGet())
    }
  }

  def add(stat: SocketApiStat, delta: Long) {
    stats(stat).addAndGet(delta)
    if (stat.isCumulative) {
      foreach(stat, _.addAndGet(delta))
    }
  }

  def get(stat: SocketApiStat) = stats(stat).get()

  def getStats(stat: SocketApiStat) = Map(
    NAME_TOTAL -> List(stats(stat).get()),
    NAME_MINUTES -> aggregatedStatsToOutputMap(stat, minutelyStats, minute, MINUTES),
    NAME_HOURS -> aggregatedStatsToOutputMap(stat, hourlyStats, hour, HOURS),
    NAME_DAYS -> aggregatedStatsToOutputMap(stat, dailyStats, day, DAYS))

  def report() {
    val runtime = Runtime.getRuntime
    val usedMemory = (runtime.totalMemory - runtime.freeMemory) / 1024L / 1024L
    val totalMemory = runtime.totalMemory / 1024L / 1024L
    val maxMemory = runtime.maxMemory / 1024L / 1024L

    info("Connections: %s | Sessions: %s".format(get(CONNECTIONS), get(SESSIONS)))
    info("Mem: %sm / %sm / %sm".format(usedMemory, totalMemory, maxMemory))
  }

  private def reset(stat: SocketApiStat, aggregate: SocketApiStatsMap) {
    if (stat.isCumulative) aggregate(stat).getAndSet(0)
  }

  // TODO: setToTotal should be an AVG of the next level down aggregation - i.e. setToTotal(hour) = avg(minutely(stat))
  private def setToTotal(stat: SocketApiStat, aggregate: SocketApiStatsMap) {
    if (!stat.isCumulative) aggregate(stat).getAndSet(stats(stat).get())
  }

  private def foreach(stat: SocketApiStat, fun: AtomicLong => Unit) {
    fun(minutelyStats(minute)(stat))
    fun(hourlyStats(hour)(stat))
    fun(dailyStats(day)(stat))
  }

  private def aggregatedStatsToOutputMap(stat: SocketApiStat,
                                         aggregate: List[SocketApiStatsMap],
                                         currentTime: Int,
                                         totalTime: Int) = {
    // index is 0 for current time, 1 for 1 $time ago, 2 for 2 $time ago, etc.
    aggregate.zipWithIndex.map {
      case (stats: SocketApiStatsMap, time) => (totalTime - time + currentTime) % totalTime -> stats(stat).get()
    }.sortBy {
      case (time, statValue) => time
    }.map {
      // replace 0th stat with live value for non-cumulative stats
      case (time, statValue) => time == 0 && !stat.isCumulative match {
        case true  => stats(stat).get()
        case false => statValue
      }
    }
  }

  private def makeStatsMap() = values.toList.map(_ -> new AtomicLong(0)).toMap

}

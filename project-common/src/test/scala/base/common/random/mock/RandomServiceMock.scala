/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 3:56 PM
 */

package base.common.random.mock

import base.common.logging.Loggable
import base.common.random.RandomService
import base.common.random.impl.RandomServiceImpl

import scala.util.Random

/**
 * Pre-populates $range number of hashes allowing peeks at what's coming
 * @author rconrad
 */
class RandomServiceMock(range: Int = RandomServiceMock.DEFAULT_RANGE,
                        intMin: Int = 0,
                        intMax: Int = Integer.MAX_VALUE) extends RandomService with Loggable {

  private val impl = new RandomServiceImpl()

  private var intCount = 0
  private var md5Count = 0
  private var sha256Count = 0
  private var uuidCount = 0

  val ints = List.range(0, range).map(i => impl.int(intMin, intMax))
  val md5s = List.range(0, range).map(i => impl.md5)
  val sha256s = List.range(0, range).map(i => impl.sha256)
  val uuids = List.range(0, range).map(i => impl.uuid)

  def int(min: Int, max: Int) = {
    debug("%s:%s, %s:%s", min, intMin, max, intMax)
    val r = ints(intCount)
    intCount += 1
    r
  }

  def md5 = {
    val r = md5s(md5Count)
    md5Count += 1
    r
  }

  def sha256 = {
    val r = sha256s(sha256Count)
    sha256Count += 1
    r
  }

  def uuid = {
    val r = uuids(uuidCount)
    uuidCount += 1
    r
  }

  lazy val random = new Random(1)

  def nextInt(i: Int = 0) = ints(intCount + i)
  def nextMd5(i: Int = 0) = md5s(md5Count + i)
  def nextSha256(i: Int = 0) = sha256s(sha256Count + i)
  def nextUuid(i: Int = 0) = uuids(uuidCount + i)

}

object RandomServiceMock {

  val DEFAULT_RANGE = 1000

}

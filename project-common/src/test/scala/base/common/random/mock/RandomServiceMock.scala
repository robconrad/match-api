/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.random.mock

import base.common.logging.Loggable
import base.common.random.RandomService
import base.common.random.impl.RandomServiceImpl

/**
 * Prepopulates $range number of hashes allowing peeks at what's coming
 * @author rconrad
 */
class RandomServiceMock(range: Int = RandomServiceMock.DEFAULT_RANGE) extends RandomService with Loggable {

  private val random = new RandomServiceImpl()

  private var md5Count = 0
  private var sha256Count = 0
  private var uuidCount = 0

  val md5s = List.range(0, range).map(i => random.md5)
  val sha256s = List.range(0, range).map(i => random.sha256)
  val uuids = List.range(0, range).map(i => random.uuid)

  def md5 = {
    val r = md5s(md5Count)
    debug(s"new md5 $r")
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

  def nextMd5(i: Int = 0) = md5s(md5Count + i)
  def nextSha256(i: Int = 0) = sha256s(sha256Count + i)
  def nextUuid(i: Int = 0) = uuids(uuidCount + i)

}

object RandomServiceMock {

  val DEFAULT_RANGE = 1000

}

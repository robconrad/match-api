/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 8:13 PM
 */

package base.entity.kv

import base.common.service.{ Service, ServiceCompanion }
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.KeyChannel
import redis.client.RedisClient

/**
 * {{ Describe the high level purpose of KvService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] trait KvService extends Service {

  final def serviceManifest = manifest[KvService]

  private[kv] def client: RedisClient
  def pipeline: Pipeline

  def makeHashKeyFactory(ch: String): HashKeyFactory
  def makeIntKeyFactory(ch: String): IntKeyFactory
  def makeSetKeyFactory(ch: String): SetKeyFactory

}

object KvService extends ServiceCompanion[KvService]

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 6:28 PM
 */

package base.entity.kv

import base.common.service.{ Service, ServiceCompanion }
import base.entity.kv.Key.Pipeline
import redis.client.RedisClient

/**
 * {{ Describe the high level purpose of KvService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] trait KvFactoryService extends Service {

  final def serviceManifest = manifest[KvFactoryService]

  private[kv] def client: RedisClient
  def pipeline: Pipeline

}

object KvFactoryService extends ServiceCompanion[KvFactoryService]

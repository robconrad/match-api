/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 12:25 PM
 */

package base.entity.kv

import base.common.service.{ Service, ServiceCompanion }
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

}

object KvService extends ServiceCompanion[KvService]

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.kv

import base.common.service.{ Service, ServiceCompanion }
import scredis.Client

/**
 * {{ Describe the high level purpose of KvService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] trait KeyCommandsService extends Service {

  final val serviceManifest = manifest[KeyCommandsService]

  def client: Client

}

object KeyCommandsService extends ServiceCompanion[KeyCommandsService]

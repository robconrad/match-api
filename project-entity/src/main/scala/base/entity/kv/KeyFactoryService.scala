/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:54 PM
 */

package base.entity.kv

import base.common.service.{ Service, ServiceCompanion }

/**
 * {{ Describe the high level purpose of KvService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait KeyFactoryService extends Service {

  final val serviceManifest = manifest[KeyFactoryService]

  def make[T](id: Any)(implicit m: Manifest[T]): T

}

object KeyFactoryService extends ServiceCompanion[KeyFactoryService]

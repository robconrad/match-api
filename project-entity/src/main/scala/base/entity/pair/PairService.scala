/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 7:06 PM
 */

package base.entity.pair

import base.common.service.{ Service, ServiceCompanion }

/**
 * Pair CRUD, etc.
 * @author rconrad
 */
trait PairService extends Service {

  final def serviceManifest = manifest[PairService]

}

object PairService extends ServiceCompanion[PairService] {

}

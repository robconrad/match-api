/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 7:59 PM
 */

package base.entity.pair

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.pair.model.PairModel

import scala.concurrent.Future

/**
 * Pair CRUD, etc.
 * @author rconrad
 */
trait PairService extends Service {

  final def serviceManifest = manifest[PairService]

  def getPairs(userId: UUID)(implicit p: Pipeline): Future[Either[ApiError, List[PairModel]]]

}

object PairService extends ServiceCompanion[PairService] {

}

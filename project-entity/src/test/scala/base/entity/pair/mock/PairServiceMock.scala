/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:37 PM
 */

package base.entity.pair.mock

import java.util.UUID

import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.pair.PairService
import base.entity.pair.model.PairModel

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of PairServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PairServiceMock(getPairsResult: Future[Either[ApiError, List[PairModel]]] = Future.successful(Right(List())))
    extends PairService {

  def getPairs(userId: UUID)(implicit p: Pipeline) = getPairsResult

}

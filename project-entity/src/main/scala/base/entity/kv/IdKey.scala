/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 3:32 PM
 */

package base.entity.kv

import java.util.UUID

import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of IntKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IdKey extends Key {

  def get(implicit p: Pipeline): Future[Option[UUID]]
  def set(v: UUID)(implicit p: Pipeline): Future[Boolean]

}

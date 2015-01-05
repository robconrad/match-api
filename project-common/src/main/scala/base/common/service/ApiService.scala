/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 3:08 PM
 */

package base.common.service

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of ServerService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait ApiService extends Service {

  def name: String

  def start(): Future[Boolean]

  def stop(): Future[Boolean]

}

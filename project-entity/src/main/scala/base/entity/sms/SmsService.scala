/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:58 PM
 */

package base.entity.sms

import base.common.service.{ Service, ServiceCompanion }

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SmsService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SmsService extends Service {

  final val serviceManifest = manifest[SmsService]

  def send(to: String, body: String): Future[Boolean]

}

object SmsService extends ServiceCompanion[SmsService]

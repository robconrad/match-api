/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 5:01 PM
 */

package base.entity.sms.mock

import base.entity.sms.SmsService

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SmsServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SmsServiceMock(result: Boolean) extends SmsService {

  def send(to: String, body: String) = Future.successful(result)

}

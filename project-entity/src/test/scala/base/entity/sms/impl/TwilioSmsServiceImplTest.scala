/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 5:02 PM
 */

package base.entity.sms.impl

import base.entity.service.EntityServiceTest

/**
 * {{ Describe the high level purpose of SmsServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TwilioSmsServiceImplTest extends EntityServiceTest {

  val service = new TwilioSmsServiceImpl(
    "AC1a2e44f13487a05dbb62cf80eaea345c",
    "8ea01c9e5b79c9ba4624f896fa91a051",
    "+14243544465")

  //  test("test sms") {
  //    assert(service.send("310-666-6453", "hello!").await())
  //  }

}

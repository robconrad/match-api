/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 4:44 PM
 */

package base.entity.sms.impl

import base.common.service.ServiceImpl
import base.entity.sms.SmsService
import com.twilio.sdk.{ TwilioRestException, TwilioRestClient }
import org.apache.http.message.BasicNameValuePair

import scala.collection.JavaConversions._
import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SmsServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TwilioSmsServiceImpl(sid: String, token: String, from: String) extends ServiceImpl with SmsService {

  private lazy val client = new TwilioRestClient(sid, token)
  private lazy val messageFactory = client.getAccount.getMessageFactory

  private lazy val fromPair = new BasicNameValuePair("From", from)

  def send(to: String, body: String) = Future {
    try {
      val params = List(
        new BasicNameValuePair("Body", body),
        new BasicNameValuePair("To", to),
        fromPair)
      messageFactory.create(params)
      true
    } catch {
      case e: TwilioRestException =>
        error("Twilio threw exception", e)
        false
    }
  }

}
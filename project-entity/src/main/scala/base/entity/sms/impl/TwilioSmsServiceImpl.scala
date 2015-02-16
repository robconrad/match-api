/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.sms.impl

import base.common.service.ServiceImpl
import base.entity.sms.SmsService
import com.twilio.sdk.{ TwilioRestClient, TwilioRestException }
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
      case e: TwilioRestException if e.getMessage.contains("is not a valid phone number") =>
        debug("Twilio threw phone number exception for '%s'", e, to)
        false
      case e: TwilioRestException =>
        error("Twilio threw exception", e)
        false
    }
  }

}

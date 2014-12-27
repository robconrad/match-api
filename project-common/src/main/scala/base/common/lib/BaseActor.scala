/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:28 PM
 */

package base.common.lib

import akka.actor.Actor
import base.common.logging.{ LoggerToken, TokenLoggable }

import scala.util.Failure

/**
 * Common functions for all Base actors
 * @author rconrad
 */
trait BaseActor extends Actor with Dispatchable with TokenLoggable {

  protected implicit var t: LoggerToken = _

  override def preStart() = {
    t = LoggerToken()
  }

  /**
   * Process an unexpected message type (log it and tell the sender they done goofed)
   */
  protected def processUnexpectedMessage(msg: Any)(implicit t: LoggerToken) {
    msg match {
      case e: Throwable =>
        error("received throwable", e)
        throw e
      case Failure(e) =>
        error("received failure", e)
        throw e
      case _ =>
        error("received unexpected message type: %s", msg)
    }
  }

  /**
   * Sends a response message to the sender unless that sender is this actor
   */
  protected def sendIgnoreSelf[T](msg: T) {
    if (sender != self) {
      sender ! msg
    }
  }

}

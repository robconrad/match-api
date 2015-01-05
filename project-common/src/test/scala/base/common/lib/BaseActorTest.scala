/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 5:12 PM
 */

package base.common.lib

import akka.actor.Props
import akka.pattern.ask
import akka.testkit.TestActorRef
import base.common.test.BaseSuite

import scala.util.Failure

/**
 * Tests common functions for all actors
 * @author rconrad
 */
class BaseActorTest extends BaseSuite {

  implicit val system = Actors.actorSystem

  private case object Expected
  private case object Unexpected
  private case object SendIgnoreSelf

  private val throwable = new RuntimeException("error")
  private val failure = Failure(throwable)

  private val actor = TestActorRef(Props(new BaseActor {
    def receive: Receive = {
      case Expected       => sender ! Expected
      case SendIgnoreSelf => sendIgnoreSelf(SendIgnoreSelf)
      case msg =>
        try {
          processUnexpectedMessage(msg)
          sender ! msg
        } catch {
          case t: Throwable => sender ! t
        }
    }
  }).withDispatcher(Actors.defaultDispatcher))

  test("processUnexpectedMessage") {
    assert((actor ? Expected).await() == Expected)
    assert((actor ? Unexpected).await() == Unexpected)
    assert((actor ? throwable).await() == throwable)
    assert((actor ? failure).await() == throwable)
  }

  test("sendIgnoreSelf") {
    assert((actor ? SendIgnoreSelf).await() == SendIgnoreSelf)
  }

}

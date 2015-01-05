/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 6:35 PM
 */

package base.common.lib

import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

import akka.actor.Actor
import akka.pattern.ask
import akka.testkit.TestActorRef
import base.common.lib.Exceptions.{ RestartActorRuntimeException, ResumeActorRuntimeException, StopActorRuntimeException }
import base.common.test.TestExceptions.TestRuntimeException
import base.common.test.{ BaseSuite, TestTiming }

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Tests that the SupervisorStrategy we have defined correctly restart, resumes, or stops actors
 *  based on the different types of runtime exceptions it handles
 * @author rconrad
 */
class ActorSupervisorTest extends BaseSuite with TestTiming {
  implicit val system = Actors.actorSystem

  case object Get
  case object ThrowRestartable
  case object ThrowRestartableSocketTimeout
  case object ThrowRestartableIllegalArgument
  case object ThrowRestartableNullPointer
  case object ThrowRestartableTestRuntime
  case object ThrowResumable
  case object ThrowStoppable

  val response = 42

  val duration = 80.millis

  // actor in our default system will use our default strategy
  val actorRef = TestActorRef(new Actor {
    def receive = {
      case Get                             => sender ! response
      case ThrowRestartable                => throw new RestartActorRuntimeException("Failed!")
      case ThrowRestartableSocketTimeout   => throw new SocketTimeoutException("Failed!")
      case ThrowRestartableIllegalArgument => throw new IllegalArgumentException("Failed!")
      case ThrowRestartableNullPointer     => throw new NullPointerException("Failed!")
      case ThrowRestartableTestRuntime     => throw new TestRuntimeException("Failed!")
      case ThrowResumable                  => throw new ResumeActorRuntimeException("Failed!")
      case ThrowStoppable                  => throw new StopActorRuntimeException("Failed!")
    }
  })

  def assertRestartable(o: Object) {
    actorRef ! o
    val result = actorRef.ask(Get)(duration).mapTo[Int]
    assert(Await.result(result, duration) == response)
  }

  test(s"$response is returned normally") {
    val result = actorRef.ask(Get)(duration).mapTo[Int]
    assert(Await.result(result, duration) == response)
  }

  test(s"$response is returned after causing restartable throw") {
    assertRestartable(ThrowRestartable)
    assertRestartable(ThrowRestartableSocketTimeout)
    assertRestartable(ThrowRestartableIllegalArgument)
    assertRestartable(ThrowRestartableNullPointer)
    assertRestartable(ThrowRestartableTestRuntime)
  }

  test(s"$response is returned after causing resumable throw") {
    actorRef ! ThrowResumable
    val result = actorRef.ask(Get)(duration).mapTo[Int]
    assert(Await.result(result, duration) == response)
  }

  test(s"$response is not returned after causing stoppable throw") {
    actorRef ! ThrowStoppable
    val result = actorRef.ask(Get)(duration).mapTo[Int]
    intercept[TimeoutException] {
      assert(Await.result(result, duration) == response)
    }
  }

}

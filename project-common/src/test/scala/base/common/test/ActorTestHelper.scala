/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 8:45 PM
 */

package base.common.test

import base.common.lib.Dispatchable
import base.common.service.ServicesBeforeAndAfterAll
import base.common.test.TestExceptions.TestRuntimeException
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.AsyncAssertions.Waiter

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }

/**
 * Provides methods that make testing actors easier
 * @author rconrad
 */
trait ActorTestHelper extends ServicesBeforeAndAfterAll
    with BeforeAndAfterEach with TestTiming with Dispatchable {

  /**
   * onComplete handler designed to work with a Waiter to synchronously block on test results
   */
  def withWaiter[T](w: Waiter, fun: T => Unit)(result: Try[Any]) {
    w {
      result match {
        case Success(result) => fun(result.asInstanceOf[T])
        case Failure(e)      => throw e
        case r               => throw new TestRuntimeException(s"unexpected response $r")
      }
    }
    w.dismiss()
  }

  /**
   * Await until the given condition evaluates to `true` or the timeout expires, whichever comes first.
   *  Stolen from akka since their TestKit conflicts with BaseSuite
   */
  def awaitCond(msg: String, p: ⇒ Boolean, max: Duration = 1000.millis, interval: Duration = 100.millis) {
    def now: FiniteDuration = System.nanoTime.nanos

    val stop = now + max

    @tailrec
    def poll(t: Duration) {
      if (!p) {
        assert(now < stop, msg + " (timeout on awaitCond expired)")
        Thread.sleep(t.toMillis)
        poll((stop - now) min interval)
      }
    }

    poll(max min interval)
  }

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 9:56 PM
 */

package base.common.test

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ Await, Future }

/**
 * Extends generic futures to have additional methods in test code
 * @author rconrad
 */
trait PimpMyFutures extends TestTiming {

  /**
   * Gives futures an await method that essentially makes them return to being synchronous
   */
  implicit class AwaitableFuture[T](f: Future[T]) {
    def await(): T = await(defaultTimeout.duration)
    def awaitLong(): T = await(longTimeout.duration)
    def await(duration: FiniteDuration): T = {
      Await.result(f, duration)
    }
  }

}

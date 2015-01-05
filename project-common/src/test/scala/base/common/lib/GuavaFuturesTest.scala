/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 6:33 PM
 */

package base.common.lib

import java.util.concurrent.{ Callable, Executors }

import base.common.test.BaseSuite
import base.common.test.TestExceptions.TestRuntimeException
import com.google.common.util.concurrent.MoreExecutors

/**
 * Test conversion of guava future to scala future
 * @author rconrad
 */
class GuavaFuturesTest extends BaseSuite {

  val str = "string"
  val cls = new GuavaFutures {}

  test("guavaFutureToAkka") {
    val pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))
    val f = pool.submit(new Callable[String]() {
      def call() = str
    })

    assert(cls.guavaFutureToAkka(f).await() == str)

    val f2 = pool.submit(new Callable[String]() {
      def call() = throw new TestRuntimeException("fail!")
    })

    intercept[TestRuntimeException] {
      cls.guavaFutureToAkka(f2).await()
    }
  }

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.lib

import com.google.common.util.concurrent.{ FutureCallback, ListenableFuture }

import scala.concurrent.{ Future, Promise }

/**
 * Provides a convenience for converting (hard to use) guava futures to standard scala futures via a promise
 * @author rconrad
 */
trait GuavaFutures {

  implicit def guavaFutureToAkka[T](f: ListenableFuture[T]): Future[T] = {
    val p = Promise[T]()
    com.google.common.util.concurrent.Futures.addCallback(f,
      new FutureCallback[T] {
        def onSuccess(r: T) {
          p success r
        }
        def onFailure(t: Throwable) {
          p failure t
        }
      })
    p.future
  }
}

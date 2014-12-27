/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.lib

/**
 * Project level exceptions
 * @author rconrad
 */
// scalastyle:off null
object Exceptions {

  class StopActorRuntimeException(s: String, e: Throwable) extends RuntimeException(s, e) {
    def this(s: String) {
      this(s, null)
    }
  }

  class ResumeActorRuntimeException(s: String, e: Throwable) extends RuntimeException(s, e) {
    def this(s: String) {
      this(s, null)
    }
  }

  class RestartActorRuntimeException(s: String, e: Throwable) extends RuntimeException(s, e) {
    def this(s: String) {
      this(s, null)
    }
  }

}

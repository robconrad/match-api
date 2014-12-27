/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.lib

/**
 * Mixin for any class that should spawn threads from the primary actor system
 * @author rconrad
 */
trait Dispatchable {
  implicit lazy val dispatcher = Actors.actorSystem.dispatcher
}

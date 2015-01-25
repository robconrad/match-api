/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 6:46 PM
 */

package base.common.lib

/**
 * Mixin for any class that should spawn threads from the primary actor system
 * @author rconrad
 */
trait Dispatchable {
  implicit lazy val actorSystem = Actors.actorSystem
  implicit lazy val dispatcher = actorSystem.dispatcher
}

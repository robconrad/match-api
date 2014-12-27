/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:28 PM
 */

package base.common.lib

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionKey }
import base.common.server.ServerService

/**
 * Configuration of the actor system
 * @author rconrad
 */
object Actors {

  // global default actor system
  val actorSystem = ServerService().makeActorSystem()
  val actorSystemAddress = BaseExtension(actorSystem).address

  // global default dispatcher manual reference
  val defaultDispatcher = "akka.actor.default-dispatcher"

  /**
   * Our specific actor system extension impl
   */
  class BaseExtensionImpl(system: ExtendedActorSystem) extends Extension {
    def address = system.provider.getDefaultAddress
  }

  object BaseExtension extends ExtensionKey[BaseExtensionImpl]

}

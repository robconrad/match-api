/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 1:55 PM
 */

package base.common.lib

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionKey }
import base.common.service.CommonService

/**
 * Configuration of the actor system
 * @author rconrad
 */
object Actors {

  // global default actor system
  val actorSystem = CommonService().makeActorSystem()
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

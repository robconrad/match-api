/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.db

import base.common.service.{ Service, ServiceCompanion }

import scala.concurrent.Future

/**
 * Handles application of DB evolutions to the desired level specified as param
 * @author rconrad
 */
private[entity] trait EvolutionService extends Service {

  final def serviceManifest = manifest[EvolutionService]

  /**
   * Execute all necessary evolutions. Conf specifies how high we go, existing db specifies
   *  how high we are starting. Idempotent.
   */
  def evolve(): Future[Unit]

  /**
   * Get the currently deployed evolution level
   */
  def getEvolutionLevel: Future[Long]

}

object EvolutionService extends ServiceCompanion[EvolutionService]


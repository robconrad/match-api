/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:15 PM
 */

package base.entity.db.impl

import java.sql.SQLException

import base.common.service.ServiceImpl
import base.common.time.DateTimeHelper
import base.entity.Tables
import base.entity.Tables.profile.simple._
import base.entity.db.{ DbConfigService, DbService, EvolutionService }

import scala.io.Source
import scala.slick.jdbc.StaticQuery.interpolation

/**
 * Handles application of DB evolutions to the desired level specified as param
 * @author rconrad
 */
private[entity] class EvolutionServiceImpl(desiredLevel: Int)
    extends ServiceImpl with EvolutionService with DateTimeHelper {

  /**
   * Post Scripts allow scala code to execute after SQL files for an evolution have been processed.
   *  NB: post scripts CANNOT use ORM code since ORM will change over time - raw SQL must be used
   */
  private val postScripts = Map[Long, (Session) => Unit]()

  /**
   * Execute all necessary evolutions. Conf specifies how high we go, existing db specifies
   *  how high we are starting. Idempotent.
   */
  def evolve() = {
    info("checking for database evolution state")
    getEvolutionLevel.flatMap { currentLevel =>
      DbService().withTxInternal { implicit s =>
        try {
          val log = s"current evolution state is: $currentLevel, desired state is: $desiredLevel"
          currentLevel >= desiredLevel match {
            case true  => info(s"database evolution state is good: $log")
            case false => evolve(currentLevel + 1, desiredLevel)
          }
        } catch {
          case t: Throwable =>
            s.rollback()
            throw t
        }
      }
    }
  }

  /**
   * Execute evolutions specified by range parameters
   */
  private def evolve(start: Long, finish: Long)(implicit s: Session) {
    val dbType = DbConfigService().dbType
    for (i <- start to finish) {
      info("attempting evolution %s", i)

      val sql = Source.fromURL(getClass.getResource(s"/evolutions/$i.sql")).mkString
      exec(sql)

      // scalastyle:off null
      val resource = getClass.getResource(s"/evolutions/$i.$dbType.sql")
      if (resource != null) {
        val sql = Source.fromURL(resource).mkString
        info("found db-specific sql for evolution %s on %s", i, dbType)
        exec(sql)
      }
      // scalastyle:on

      postScripts.get(i) match {
        case Some(script) =>
          info("found postScript for evolution %s", i)
          script(s)
        case None => // no post script for this evolution
      }

      info(s"evolution $i completed successfully")

      val row = Tables.EvolutionRow(i, now)
      Tables.Evolutions.insert(row)
    }
  }

  private def exec(sql: String)(implicit s: Session) {
    sqlu"""#$sql""".execute
  }

  /**
   * Get the evolution id of this database, assume zero if there's an error since the
   *  evolutions table is created by evo 1.
   */
  def getEvolutionLevel =
    DbService().withTxResultInternal { implicit s =>
      try {
        Tables.Evolutions.map(_.id).sortBy(_.desc).list.head
      } catch {
        case x: SQLException => 0
      }
    }

}


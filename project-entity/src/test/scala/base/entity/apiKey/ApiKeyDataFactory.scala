/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:23 AM
 */

package base.entity.apiKey

import base.common.random.RandomService
import base.entity.Tables._
import base.entity.Tables.profile.simple._
import base.entity.db.DbService
import base.entity.test.DataFactory

/**
 * Responsible for creating and destroying test data for this table
 * @author rconrad
 */
object ApiKeyDataFactory extends DataFactory {

  def apply(row: ApiKeyRow): ApiKeyRow =
    DbService().withTxResultInternal { implicit s =>
      val id = (ApiKeys returning ApiKeys.map(_.id)) += row
      row.copy(id = id)
    }.await()

  def makeRow(): ApiKeyRow =
    ApiKeyRowBuilder(RandomService().md5.toString)

  protected def cleanup(implicit s: Session) {
    ApiKeys.delete
  }

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 10:04 PM
 */

package base.entity.user

import base.common.random.RandomService
import base.entity.Tables._
import base.entity.Tables.profile.simple._
import base.entity.auth.impl.AuthServiceImpl
import base.entity.db.DbService
import base.entity.test.DataFactory

/**
 * Responsible for creating and destroying test data for this table
 * @author rconrad
 */
object UserDataFactory extends DataFactory {

  def apply(): UserRow =
    apply(makeRow())

  def apply(email: String,
            password: String): UserRow =
    apply(makeRow(email, password, active = true))

  def apply(email: String,
            password: String,
            active: Boolean): UserRow =
    apply(makeRow(email, password, active))

  def apply(row: UserRow): UserRow =
    DbService().withTxResultInternal { implicit s =>
      val id = (Users returning Users.map(_.id)) += row
      row.copy(id = id)
    }.await()

  def makeRow(): UserRow = makeRow("emails", "password", active = true)

  def makeRow(email: String,
              password: String,
              active: Boolean): UserRow = {
    val user = UserRow(
      id = 0L,
      uuid = RandomService().uuid,
      email = email,
      passwordHash = "",
      passwordSalt = "",
      resetCode = None,
      resetExpiresAt = None,
      createdAt = now,
      active = active)

    user
  }

  protected def cleanup(implicit s: Session) {
    Users.delete
  }

}

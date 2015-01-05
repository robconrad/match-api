/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 7:16 PM
 */

package base.entity.user.impl

import java.sql.SQLException
import java.util.UUID

import base.common.service.ServiceImpl
import base.common.time.DateTimeHelper
import base.entity.ApiStrings.User._
import base.entity.ApiStrings._
import base.entity.Tables.Users
import base.entity.Tables.profile.simple._
import base.entity.auth.context.AuthContext
import base.entity.db.DbService
import base.entity.logging.AuthLoggable
import base.entity.service.CrudServiceImplHelper
import base.entity.user.UserService
import base.entity.user.model.{ PostResetRequest, PostUserRequest, PutUserRequest, UserModel }
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class UserServiceImpl
    extends ServiceImpl with UserService with CrudServiceImplHelper[UserModel] with AuthLoggable with DateTimeHelper {

  def create(implicit authCtx: AuthContext, input: PostUserRequest) = {
    debug("attempting to create user with email: %s", input.email)
    input.validate match {
      case Some(error) =>
        debug("user creation failed validation with: %s", error)
        error
      case None => DbService().withTxResult { implicit s =>
        val row = input.toRow()
        try {
          val userId = Users returning Users.map(_.id) += row
          val newRow = row.copy(id = userId)
          debug("user creation succeeded with: %s", newRow)

          UserModel(newRow)
        } catch {
          case e: SQLException if e.getMessage.contains("u_users_email_provider_id") =>
            (emailUniqueErrorDesc, BadRequest)
        }
      }
    }
  }

  def update(implicit authCtx: AuthContext, uuid: UUID, input: PutUserRequest) = {
    // note that password should only be updatable by the user himself,
    //  unlike the other fields which anyone with perms may edit
    (notYetImplementedNote, NotImplemented)
  }

  def get(implicit authCtx: AuthContext, uuid: UUID) = {
    debug("attempting to get user with uuid: %s", uuid)
    DbService().withTxResult { implicit s =>
      val q = for {
        u <- Users
        if u.uuid === uuid
      } yield u
      val user = q.list.headOption.map {
        case user => UserModel(user)
      }
      user match {
        case Some(user) => user
        case _          => (readNotFoundDesc, NotFound)
      }
    }
  }

  def resetInitiate(input: PostResetRequest) = {
    (notYetImplementedNote, NotImplemented)
  }

  def resetComplete(resetCode: String) = {
    (notYetImplementedNote, NotImplemented)
  }

}

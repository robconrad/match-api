/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 7:16 PM
 */

package base.entity.user.mock

import java.util.UUID

import base.common.random.RandomService
import base.common.service.ServiceImpl
import base.common.time.DateTimeHelper
import base.entity.Tables.UserRow
import base.entity.auth.context.AuthContext
import base.entity.service.CrudServiceImplHelper
import base.entity.user.UserService
import base.entity.user.model.{ PostResetRequest, PostUserRequest, PutUserRequest, UserModel }

/**
 * Fake UserService will do whatever you like
 * @author rconrad
 */
class UserServiceMock() extends ServiceImpl with UserService with CrudServiceImplHelper[UserModel] with DateTimeHelper {

  private var userUUID = RandomService().uuid
  private var users = Map[UUID, UserRow]()

  /**
   * Create a new User in the database and get a UserResponse
   */
  def create(implicit authCtx: AuthContext, input: PostUserRequest) = {
    val uuid = nextUserUUID
    val row = input.toRow(now).copy(uuid = uuid)
    users += uuid -> row
    UserModel(row)
  }

  /**
   * Update an User in the database and get a UserResponse
   */
  def update(implicit authCtx: AuthContext, uuid: UUID, input: PutUserRequest) =
    throw new Exception("not implemented")

  /**
   * Get an User Response from the database
   */
  def get(implicit authCtx: AuthContext, uuid: UUID) =
    throw new Exception("not implemented")

  /**
   * Begin the process of resetting a user's password. Will create a reset code and send an email
   * to the user with a link to hit resetComplete with that code
   */
  def resetInitiate(input: PostResetRequest) =
    throw new Exception("not implemented")

  /**
   * Complete the process of resetting a user's password. Users will have received a code in their
   * email which they supply to this function in order to finally set their password to whatever
   * it was assigned in the email
   */
  def resetComplete(resetCode: String) =
    throw new Exception("not implemented")

  /**
   * Get an User
   */
  def get(uuid: UUID) = users(uuid)

  /**
   * Get the next userId that will be assigned
   */
  def getNextUserUUID = userUUID

  /**
   * Get the next available invoiceId and increment
   */
  private def nextUserUUID = {
    val id = userUUID
    userUUID = RandomService().uuid
    id
  }

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.user

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.user.UserService.ErrorOrUser
import base.entity.user.model.{ PostResetRequest, PostUserRequest, PutUserRequest, User }

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait UserService extends Service {

  final def serviceManifest = manifest[UserService]

  /**
   * Create a new User in the database and get a UserResponse
   */
  def create(implicit authCtx: AuthContext, input: PostUserRequest): Future[ErrorOrUser]

  /**
   * Update an User in the database and get a UserResponse
   */
  def update(implicit authCtx: AuthContext, id: UUID, input: PutUserRequest): Future[ErrorOrUser]

  /**
   * Get an User Response from the database
   */
  def get(implicit authCtx: AuthContext, id: UUID): Future[ErrorOrUser]

  /**
   * Begin the process of resetting a user's password. Will create a reset code and send an email
   *  to the user with a link to hit resetComplete with that code
   */
  def resetInitiate(input: PostResetRequest): Future[ErrorOrUser]

  /**
   * Complete the process of resetting a user's password. Users will have received a code in their
   *  email which they supply to this function in order to finally set their password to whatever
   *  it was assigned in the email
   */
  def resetComplete(resetCode: String): Future[ErrorOrUser]

}

object UserService extends ServiceCompanion[UserService] {

  // the Invoice endpoint always returns either a EntityError or an InvoiceResponse
  type ErrorOrUser = Either[ApiError, User]

}

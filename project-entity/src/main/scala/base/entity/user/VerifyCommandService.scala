/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:17 PM
 */

package base.entity.user

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.error.ApiError
import base.entity.perm.Perms
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait VerifyCommandService extends CommandService[VerifyModel, VerifyResponseModel] {

  final def serviceManifest = manifest[VerifyCommandService]

  final val perms = Set(Perms.VERIFY)

  def sendVerifySms(phone: String, code: String): Future[Boolean]

  def makeVerifyCode(): String

  def validateVerifyCodes(code1: String, code2: String): Boolean

}

object VerifyCommandService extends CommandServiceCompanion[VerifyCommandService] {

  type VerifyResponse = Future[Either[ApiError, VerifyResponseModel]]

}
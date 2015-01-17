/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 12:29 PM
 */

package base.entity.user

import base.entity.command.model.CommandModel
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

  final def command = VerifyCommandService.cmd

  final def serviceManifest = manifest[VerifyCommandService]

  final val perms = Set(Perms.VERIFY)

  def sendVerifySms(phone: String, code: String): Future[Boolean]

  def makeVerifyCode(): String

  def validateVerifyCodes(code1: String, code2: String): Boolean

}

object VerifyCommandService extends CommandServiceCompanion[VerifyCommandService] {

  final val cmd = "verify"

  type VerifyResponse = Future[Either[ApiError, CommandModel[VerifyResponseModel]]]

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.apiKey

import base.common.service.{ Service, ServiceCompanion }
import base.entity.Tables.profile.simple.Session
import base.entity.apiKey.ApiKeysService.ErrorOrApiKeys
import base.entity.apiKey.model.ApiKeys
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError

import scala.concurrent.Future

/**
 * CRUD for API Keys
 * @author rconrad
 */
trait ApiKeysService extends Service {

  final def serviceManifest = manifest[ApiKeysService]

  /**
   * Get a list of all API keys associated with this AuthContext. Will include disabled keys for reference
   *  as well as all merchant keys if this is a provider.
   */
  def get(implicit authCtx: AuthContext): Future[ErrorOrApiKeys]

  /**
   * Get a list of all API keys associated with this AuthContext. Will include disabled keys for reference
   *  as well as all merchant keys if this is a provider.
   */
  def getWithSession(implicit authCtx: AuthContext, s: Session): ErrorOrApiKeys

  /**
   * Create new API keys for this AuthContext and return them.
   */
  def refresh(implicit authCtx: AuthContext): Future[ErrorOrApiKeys]

  /**
   * Create new API keys for this merchantId and return them.
   */
  def refreshWithSession(merchantId: Option[Long],
                         siteId: Option[Long])(implicit authCtx: AuthContext, s: Session): ErrorOrApiKeys

}

object ApiKeysService extends ServiceCompanion[ApiKeysService] {

  type ErrorOrApiKeys = Either[ApiError, ApiKeys]

}

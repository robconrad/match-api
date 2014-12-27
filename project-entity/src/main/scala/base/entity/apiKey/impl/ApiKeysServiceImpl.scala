/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:12 PM
 */

package base.entity.apiKey.impl

import base.common.service.ServiceImpl
import base.common.time.DateTimeHelper
import base.entity.Tables.profile.simple._
import base.entity.apiKey
import base.entity.apiKey.ApiKeysService
import base.entity.auth.context.AuthContext
import base.entity.db.DbService
import base.entity.error.ApiError
import base.entity.logging.AuthLoggable
import base.entity.service.CrudServiceImplHelper

/**
 * CRUD for API Keys
 * @author rconrad
 */
private[entity] class ApiKeysServiceImpl extends ServiceImpl with ApiKeysService
    with CrudServiceImplHelper[apiKey.model.ApiKeys] with DateTimeHelper with AuthLoggable {

  /**
   * Get a list of all API keys associated with this AuthContext. Will include disabled keys for reference
   * as well as all merchant keys if this is a provider.
   */
  def get(implicit authCtx: AuthContext) = {
    DbService().withTxResult { implicit s =>
      getWithSession
    }
  }

  /**
   * Get a list of all API keys associated with this AuthContext. Will include disabled keys for reference
   *  as well as all merchant keys if this is a provider, all site keys if this is a merchant, etc.
   *
   * Order returned is:
   *  - Merchant name (if present) ASC
   *    - Site name (if present) ASC
   *      - Active Keys first
   *        - Most recently created first
   *
   * NOTE: if the AuthContext user is a provider, merchant will be included with the results (aka hydrated)
   *       if not, there is no need to include merchant as there can only be one.
   */
  def getWithSession(implicit authCtx: AuthContext, s: Session) = {
    ApiError("foo")
  }

  /**
   * Create new API keys for this AuthContext and return them.
   */
  def refresh(implicit authCtx: AuthContext) = {
    DbService().withTxResult { implicit s =>
      refreshWithSession(None, None)
    }
  }

  /**
   * Create new API keys for this merchantId and return them.
   */
  def refreshWithSession(merchantId: Option[Long], siteId: Option[Long])(implicit authCtx: AuthContext, s: Session) = {
    debug("refresh api keys (merchant %s, site %s)", merchantId, siteId)
    ApiError("foo")
  }

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:52 PM
 */

package base.entity.apiKey.mock

import base.common.service.ServiceImpl
import base.entity.Tables.profile.simple._
import base.entity.apiKey.ApiKeysService
import base.entity.apiKey.model.ApiKeys
import base.entity.auth.context.AuthContext
import base.entity.service.CrudServiceImplHelper

/**
 * Fake ApiKeysService will do whatever you like
 * @author rconrad
 */
class ApiKeysServiceMock() extends ServiceImpl with ApiKeysService with CrudServiceImplHelper[ApiKeys] {

  /**
   * Get a list of all API keys associated with this AuthContext. Will include disabled keys for reference
   *  as well as all merchant keys if this is a provider.
   */
  def get(implicit authCtx: AuthContext) = ApiKeys(List())

  def getWithSession(implicit authCtx: AuthContext, s: Session) = ApiKeys(List())

  def refresh(implicit authCtx: AuthContext) = get

  def refreshWithSession(merchantId: Option[Long],
                         siteId: Option[Long])(implicit authCtx: AuthContext, s: Session) = getWithSession

}

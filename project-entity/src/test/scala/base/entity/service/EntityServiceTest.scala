/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:54 PM
 */

package base.entity.service

import base.common.service.ServiceTest
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.perm.PermException
import base.entity.test.EntityBaseSuite

/**
 * Base service test class, sets up other services etc.
 * @author rconrad
 */
private[entity] abstract class EntityServiceTest
    extends ServiceTest with EntityBaseSuite with EntityServicesBeforeAndAfterAll {

  def authCtx(implicit channelCtx: ChannelContext) = channelCtx.authCtx

  protected def assertPermException(f: ChannelContext => Unit) {
    intercept[PermException] {
      f(ChannelContextDataFactory.emptyUserAuth)
    }
  }

}

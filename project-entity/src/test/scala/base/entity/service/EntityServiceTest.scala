/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 9:48 AM
 */

package base.entity.service

import base.common.service.ServiceTest
import base.entity.auth.context.{ AuthContextDataFactory, AuthContext }
import base.entity.perm.PermException
import base.entity.test.EntityBaseSuite

/**
 * Base service test class, sets up other services etc.
 * @author rconrad
 */
private[entity] abstract class EntityServiceTest
    extends ServiceTest with EntityBaseSuite with EntityServicesBeforeAndAfterAll {

  protected def assertPermException(f: AuthContext => Unit) {
    intercept[PermException] {
      f(AuthContextDataFactory.emptyUserAuth)
    }
  }

}

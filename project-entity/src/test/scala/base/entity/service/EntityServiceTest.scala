/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.service

import base.common.service.ServiceTest
import base.entity.test.EntityBaseSuite

/**
 * Base service test class, sets up other services etc.
 * @author rconrad
 */
private[entity] abstract class EntityServiceTest
  extends ServiceTest with EntityBaseSuite with EntityServicesBeforeAndAfterAll

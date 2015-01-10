/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:31 PM
 */

package base.entity.service

import base.common.service.ServicesBeforeAndAfterAll

/**
 * Mixin that will make sure the Services registry looks the same
 *  before and after your tests run (i.e. wherein you register some mocks)
 *  NB: if you also need to use beforeAll / afterAll, be sure to call
 *      super.beforeAll / super.afterAll within.
 * @author rconrad
 */
private[entity] trait EntityServicesBeforeAndAfterAll extends ServicesBeforeAndAfterAll {

}

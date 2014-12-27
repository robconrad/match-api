/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:07 PM
 */

package base.entity.auth.impl

import base.entity.Tables._
import base.entity.apiKey.ApiKeyDataFactory
import base.entity.auth.context._
import base.entity.service.EntityServiceTest
import org.scalatest.BeforeAndAfterEach

/**
 * Responsible for testing KEY-BASED authentication (for USER-BASED see AuthServiceImplUsersTest).
 *  Runs all tests against all possible ApiKeyTypes, checks for disabled everything (providers, merchants, keys),
 *  attempts login with bad or missing keys, etc.
 * @author rconrad
 */
class AuthServiceImplKeysTest() extends EntityServiceTest with BeforeAndAfterEach {

  override protected val shouldSetupAndCleanDb = true

  val service = new AuthServiceImpl()

  private var secretKey: ApiKeyRow = _
  private var contextParams: AuthContextParams = _

  private val invoiceHash = "invoiceHash"

  private val key = "secretKey"

  /**
   * Destroy all keys after each test
   */
  override def afterEach() {
    ApiKeyDataFactory.cleanup()
  }

}

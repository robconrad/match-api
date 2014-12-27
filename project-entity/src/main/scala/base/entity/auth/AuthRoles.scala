/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:45 PM
 */

package base.entity.auth

/**
 * Defines the different roles of authentication that may be used against the API
 * @author rconrad
 */
object AuthRoles extends Enumeration {
  type AuthRole = Value

  val PUBLIC = Value // no auth provided, only for openly accessible endpoints
  val PRIVILEGED = Value // auth provided, can access privileged endpoints

}

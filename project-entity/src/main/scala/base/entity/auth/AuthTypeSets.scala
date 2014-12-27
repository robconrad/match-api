/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:10 PM
 */

package base.entity.auth

/**
 * The available sets of authentication types that may be associated with particular endpoints
 * @author rconrad
 */
object AuthTypeSets {

  val USER = AuthTypeSet(
    AuthTypes.USER)

  val KEY = AuthTypeSet(
    AuthTypes.KEY)

  val USER_OR_KEY = AuthTypeSet(
    AuthTypes.USER,
    AuthTypes.KEY)

}

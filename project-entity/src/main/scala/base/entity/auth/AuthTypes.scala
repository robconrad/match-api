/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:07 PM
 */

package base.entity.auth

/**
 * Defines the different types of authentication that may be used against
 *  the API as well as logging chars to represent them
 * @author rconrad
 */
object AuthTypes extends Enumeration {
  type AuthType = Value

  implicit class Props(v: Value) {
    /**
     * Whether the type of authentication is public or not indicates whether certain information  should be hidden
     *  from responses. This is necessary because end users have SiteKeyAuth the same way a merchant CSR might have
     *  SiteUserAuth, so another mechanism is necessary to distinguish them.s
     */
    def isPublic = v match {
      case PUBLIC => true
      case USER   => false
    }

    /**
     * Blank out an optional value if the type of auth is public
     */
    def hideIfPublic[T](o: T): Option[T] = hideIfPublic(Option(o))
    def hideIfPublic[T](o: Option[T]): Option[T] = isPublic match {
      case true  => None
      case false => o
    }

    /**
     * Provide a short log prefix string to represent the different types of auth
     */
    def toPrefix = v match {
      case PUBLIC => "PUB"
      case USER   => "USR"
    }
  }

  val PUBLIC = Value
  val USER = Value
  val KEY = Value

}

/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest

import base.rest.Locations.Location
import base.rest.Versions._

/**
 * Representation of a REST endpoint (should be avoided for anything version-sensitive
 *  as it always assumes latest version)
 * @author rconrad
 */
private[rest] case class Endpoint(location: Option[Location], endpoint: String) {

  override def toString = toString(latest)
  def toString(version: Version) = VersionedEndpoint(version, location, endpoint).toString

  def toPathMatcherLatest = toPathMatcher(latest)
  def toPathMatcher(version: Version) = VersionedEndpoint(version, location, endpoint).toPathMatcher

}

private[rest] object Endpoint {

  def apply(location: Location): Endpoint = {
    apply(Option(location), "")
  }

  def apply(location: Location, endpoint: String): Endpoint = {
    apply(Option(location), endpoint)
  }

  def apply(endpoint: String): Endpoint = {
    apply(None, endpoint)
  }

  implicit def asString(endpoint: Endpoint) = endpoint.toString
  implicit def asPathMatcher(endpoint: Endpoint) = endpoint.toPathMatcherLatest

  /**
   * Names that can be used in swagger annotations describing the API.
   * Coverage is turned off since it can't actually determine whether final vals have been covered (weak, I know)
   */
  // $COVERAGE-OFF$
  object Names {

    final val ADMIN = "admin"

    final val AUTH = "auth"

    final val API_KEYS = "keys"

    final val INVOICES = "invoices"
    final val PATH_INVOICE_RATES = "/{id}/exchange_rates"

    final val EXCHANGE_RATES = "exchange_rates"

    final val IPNS = "ipns"

    final val PROVIDERS = "providers"

    final val MERCHANTS = "merchants"

    final val SITES = "sites"

    final val USERS = "users"

    final val USERS_ME = "me"
    final val PATH_USERS_ME = "/me"

    final val USERS_RESET = "reset"
    final val PATH_USERS_RESET = "/reset"

  }
  // $COVERAGE-ON$

  val ADMIN = apply(Names.ADMIN)

  val API_KEYS = apply(Names.API_KEYS)

  val AUTH = apply(Names.AUTH)

  val DOCS = apply(Locations.DOCS)

  val INVOICES = apply(Names.INVOICES)

  val IPNS = apply(Names.IPNS)

  val PROVIDERS = apply(Names.PROVIDERS)

  val MERCHANTS = apply(Names.MERCHANTS)

  val REST = apply("")

  val SITES = apply(Names.SITES)

  val USERS = apply(Names.USERS)
  val USERS_ME = apply(Locations.USERS, Names.USERS_ME)
  val USERS_RESET = apply(Locations.USERS, Names.USERS_RESET)

}

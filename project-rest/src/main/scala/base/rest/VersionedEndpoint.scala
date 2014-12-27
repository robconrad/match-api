/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest

import base.rest.Locations.Location
import base.rest.Versions._
import spray.routing.directives.PathDirectives

/**
 * Representation of a REST endpoint (which *always* requires a version, and almost
 *  always requires a location)
 * @author rconrad
 */
private[rest] case class VersionedEndpoint(version: Version,
                                           location: Option[Location],
                                           endpoint: String) extends PathDirectives {
  // render endpoints as URLs
  override val toString = "/" + List[String](
    Locations.REST,
    version,
    location.map(_.toString).getOrElse(""),
    endpoint
  ).filter(_ != "").mkString("/")

  // convert endpoint to a Spray PathMatcher for use in route selection
  val toPathMatcher = location match {
    case Some(location) => Locations.REST.toString / version.toString / location.toString / endpoint
    case _              => Locations.REST.toString / version.toString / endpoint
  }
}

private[rest] object VersionedEndpoint {

  def apply(version: Version, location: Location): VersionedEndpoint = {
    apply(version, Option(location), "")
  }

  def apply(version: Version, endpoint: String): VersionedEndpoint = {
    apply(version, None, endpoint)
  }

  implicit def asString(endpoint: VersionedEndpoint) = endpoint.toString
  implicit def asPathMatcher(endpoint: VersionedEndpoint) = endpoint.toPathMatcher

  val DOCS_LATEST = apply(latest, Locations.DOCS)
  val ADMIN_LATEST = apply(latest, "admin")
  val INVOICES_LATEST = apply(latest, "invoices")

  val restAvailable = Versions.available.map(apply(_, ""))

  private val baseAvailable = Set(DOCS_LATEST, INVOICES_LATEST)
  val available = Map(
    V03 -> baseAvailable.map(_.copy(version = V03))
  )

  available.foreach {
    case (version, available) =>
      assert(Versions.available.contains(version))
  }

  Versions.available.foreach { version =>
    assert(available.keys.toSet.contains(version))
  }

}

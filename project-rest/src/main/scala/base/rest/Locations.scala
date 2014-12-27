/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest

/**
 * Representation of a location (aka path segment) in the URL space of the server.
 *  Includes both top-level and interstitial segments.
 * @author rconrad
 */
private[rest] object Locations extends Enumeration {
  type Location = Value
  implicit def asString(v: Location) = v.toString

  // base location for REST API
  val REST = Value("rest")

  // sub-structure underneath /rest/ for Swagger JSON responses
  val DOCS = Value("docs")

  val USERS = Value("users")

  // completely separate location (i.e. outside /rest/) for HTML DOCs
  val REST_DOCS = Value("rest-docs")

  // completely separate location (i.e. outside /rest/) for HTML Hackery
  val STATIC = Value("static")

}

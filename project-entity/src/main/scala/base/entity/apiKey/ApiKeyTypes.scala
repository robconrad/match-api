/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:40 AM
 */

package base.entity.apiKey

import base.entity.ApiStrings

/**
 * Various types of our API Keys that can be used by clients
 * @author rconrad
 */
object ApiKeyTypes extends Enumeration {
  type ApiKeyType = Value

  implicit def asString(v: ApiKeyType) = v.toString

  def get(in: String) = values.find(_.toString == in)

  implicit class Props(v: Value) {
    def header = authHeaders(v)
    lazy val lowercaseName = header.toLowerCase
  }

  val API = Value

  /**
   * Custom auth headers are X-Base-Key-{KEY TYPE}, e.g. X-Base-Key-API
   */
  private val headerPrefix = ApiStrings.headerPrefix + "Key-"
  val authHeaders = values.map { key =>
    key -> (headerPrefix + key.toString.substring(0, 1) + key.substring(1).toLowerCase)
  }.toMap

}


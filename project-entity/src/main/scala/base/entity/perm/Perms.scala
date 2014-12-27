/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.perm

/**
 * Permissions to access particular areas of functionality in the API.
 * @author rconrad
 */
object Perms extends Enumeration {
  type Perm = Value

  val API_KEY_READ = Value
  val API_KEY_REFRESH = Value

  val AUTH_READ = Value

  val INVOICE_CREATE = Value
  val INVOICE_UPDATE = Value
  val INVOICE_READ = Value
  val INVOICE_SEARCH = Value

  val IPN_SEND = Value

  val MERCHANT_CREATE = Value
  val MERCHANT_UPDATE = Value
  val MERCHANT_READ = Value
  val MERCHANT_SEARCH = Value

  val SITE_CREATE = Value
  val SITE_UPDATE = Value
  val SITE_READ = Value
  val SITE_SEARCH = Value

  val PROVIDER_READ = Value

  val USER_CREATE = Value
  val USER_UPDATE = Value
  val USER_READ = Value
  val USER_READ_ME = Value

}

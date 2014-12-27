/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.perm

/**
 * Logical groupings of permissions that are often granted together (e.g. READ_ONE and READ_MANY could be
 *  granted together as the set READ)
 * @author rconrad
 */
private[perm] object PermSets {

  val apiKeyRead = PermSet(
    Perms.API_KEY_READ
  )

  val apiKeyModify = PermSet(
    Perms.API_KEY_REFRESH
  )

  val authRead = PermSet(
    Perms.AUTH_READ
  )

  val invoiceModify = PermSet(
    Perms.INVOICE_CREATE,
    Perms.INVOICE_UPDATE
  )

  val invoiceUpdate = PermSet(
    Perms.INVOICE_UPDATE
  )

  val invoiceRead = PermSet(
    Perms.INVOICE_READ
  )

  val invoiceSearch = PermSet(
    Perms.INVOICE_READ,
    Perms.INVOICE_SEARCH
  )

  val ipn = PermSet(
    Perms.IPN_SEND
  )

  val merchantModify = PermSet(
    Perms.MERCHANT_CREATE,
    Perms.MERCHANT_UPDATE
  )

  val merchantRead = PermSet(
    Perms.MERCHANT_READ
  )

  val merchantSearch = PermSet(
    Perms.MERCHANT_READ,
    Perms.MERCHANT_SEARCH
  )

  val merchantUpdate = PermSet(
    Perms.MERCHANT_UPDATE
  )

  val providerRead = PermSet(
    Perms.PROVIDER_READ
  )

  val siteModify = PermSet(
    Perms.SITE_CREATE,
    Perms.SITE_UPDATE
  )

  val siteRead = PermSet(
    Perms.SITE_READ
  )

  val siteSearch = PermSet(
    Perms.SITE_READ,
    Perms.SITE_SEARCH
  )

  val siteUpdate = PermSet(
    Perms.SITE_UPDATE
  )

  val userReadMe = PermSet(
    Perms.USER_READ_ME
  )

  val userRead = PermSet(
    Perms.USER_READ,
    Perms.USER_READ_ME
  )

  val userModify = PermSet(
    Perms.USER_CREATE,
    Perms.USER_UPDATE
  )

}

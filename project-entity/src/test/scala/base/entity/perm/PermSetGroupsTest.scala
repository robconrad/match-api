/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:58 PM
 */

package base.entity.perm

import base.entity.test.EntityBaseSuite

/**
 * Essentially a gold file for permissions groups -- asserts that every group has exactly and only the
 *  permissions explicitly defined in this file
 * @author rconrad
 */
class PermSetGroupsTest extends EntityBaseSuite {

  import base.entity.perm.PermSetGroups._
  import base.entity.perm.Perms._

  private def assertGroup(group: PermSetGroup, expected: Set[Perm]) {
    expected.foreach { perm =>
      assert(group.contains(perm), "actual perms is missing expected perm")
    }
    group.permSet.set.foreach { perm =>
      assert(expected.contains(perm), "expected perms is missing actual perm")
    }
  }

  test("public") {
    assertGroup(public, Set(
      PROVIDER_READ))
  }

  test("siteUser") {
    assertGroup(siteUser, Set(
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      IPN_SEND,
      PROVIDER_READ,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH))
  }

  test("sitePublishableKey") {
    assertGroup(sitePublishableKey, Set(
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      PROVIDER_READ))
  }

  test("siteSecretKey") {
    assertGroup(siteSecretKey, Set(
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      IPN_SEND,
      PROVIDER_READ,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH))
  }

  test("merchantUser") {
    assertGroup(merchantUser, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      MERCHANT_READ,
      MERCHANT_UPDATE,
      PROVIDER_READ,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

  test("merchantUserWithSiteContext") {
    assertGroup(merchantUserWithSiteContext, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      IPN_SEND,
      MERCHANT_READ,
      MERCHANT_UPDATE,
      PROVIDER_READ,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

  test("merchantPublishableKey") {
    assertGroup(merchantPublishableKey, Set(
      AUTH_READ,
      PROVIDER_READ))
  }

  test("merchantPublishableKeyWithSiteContext") {
    assertGroup(merchantPublishableKeyWithSiteContext, Set(
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      PROVIDER_READ))
  }

  test("merchantSecretKey") {
    assertGroup(merchantSecretKey, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      MERCHANT_READ,
      MERCHANT_UPDATE,
      PROVIDER_READ,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ))
  }

  test("merchantSecretKeyWithSiteContext") {
    assertGroup(merchantSecretKeyWithSiteContext, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      IPN_SEND,
      MERCHANT_READ,
      MERCHANT_UPDATE,
      PROVIDER_READ,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ))
  }

  test("providerUser") {
    assertGroup(user, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_READ,
      INVOICE_SEARCH,
      MERCHANT_CREATE,
      MERCHANT_UPDATE,
      MERCHANT_READ,
      MERCHANT_SEARCH,
      PROVIDER_READ,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

  test("providerUserWithMerchantContext") {
    assertGroup(providerUserWithMerchantContext, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      MERCHANT_UPDATE,
      MERCHANT_READ,
      MERCHANT_SEARCH,
      PROVIDER_READ,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

  test("providerUserWithSiteContext") {
    assertGroup(providerUserWithSiteContext, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      MERCHANT_UPDATE,
      MERCHANT_READ,
      MERCHANT_SEARCH,
      IPN_SEND,
      PROVIDER_READ,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

  test("providerPublishableKeyWithMerchantContext") {
    assertGroup(providerPublishableKeyWithMerchantContext, Set(
      AUTH_READ,
      PROVIDER_READ))
  }

  test("providerPublishableKeyWithSiteContext") {
    assertGroup(providerPublishableKeyWithSiteContext, Set(
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      PROVIDER_READ))
  }

  test("providerSecretKey") {
    assertGroup(providerSecretKey, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_READ,
      INVOICE_SEARCH,
      MERCHANT_CREATE,
      MERCHANT_UPDATE,
      MERCHANT_READ,
      MERCHANT_SEARCH,
      PROVIDER_READ,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

  test("providerSecretKeyWithMerchantContext") {
    assertGroup(providerSecretKeyWithMerchantContext, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      MERCHANT_UPDATE,
      MERCHANT_READ,
      MERCHANT_SEARCH,
      SITE_CREATE,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      PROVIDER_READ,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

  test("providerSecretKeyWithSiteContext") {
    assertGroup(providerSecretKeyWithSiteContext, Set(
      API_KEY_READ,
      API_KEY_REFRESH,
      AUTH_READ,
      INVOICE_CREATE,
      INVOICE_UPDATE,
      INVOICE_READ,
      INVOICE_SEARCH,
      IPN_SEND,
      MERCHANT_UPDATE,
      MERCHANT_READ,
      MERCHANT_SEARCH,
      PROVIDER_READ,
      SITE_READ,
      SITE_UPDATE,
      SITE_SEARCH,
      USER_CREATE,
      USER_UPDATE,
      USER_READ,
      USER_READ_ME))
  }

}

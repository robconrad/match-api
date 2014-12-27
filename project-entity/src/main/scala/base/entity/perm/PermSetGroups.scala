/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:56 PM
 */

package base.entity.perm

/**
 * Collections of PermSets that are granted to particular profiles of AuthContexts.
 * @author rconrad
 */
object PermSetGroups {

  val public = PermSetGroup.sets(
    PermSets.providerRead
  )

  val siteUser = PermSetGroup.sets(
    PermSets.authRead,
    PermSets.invoiceModify,
    PermSets.invoiceSearch,
    PermSets.ipn,
    PermSets.providerRead,
    PermSets.siteSearch,
    PermSets.siteUpdate
  )

  val sitePublishableKey = PermSetGroup.sets(
    PermSets.authRead,
    PermSets.providerRead,
    PermSets.invoiceModify,
    PermSets.invoiceRead
  )

  val siteSecretKey = PermSetGroup.groups(
    siteUser
  )

  val merchantUser = PermSetGroup.sets(
    PermSets.apiKeyRead,
    PermSets.apiKeyModify,
    PermSets.authRead,
    PermSets.invoiceUpdate,
    PermSets.invoiceSearch,
    PermSets.merchantRead,
    PermSets.merchantUpdate,
    PermSets.providerRead,
    PermSets.siteModify,
    PermSets.siteSearch,
    PermSets.userReadMe,
    PermSets.userRead,
    PermSets.userModify
  )

  val merchantUserWithSiteContext = PermSetGroup.groups(
    merchantUser,
    siteUser
  )

  val merchantPublishableKey = PermSetGroup.sets(
    PermSets.authRead,
    PermSets.providerRead
  )

  val merchantPublishableKeyWithSiteContext = PermSetGroup.groups(
    sitePublishableKey
  )

  val merchantSecretKey = PermSetGroup.groups(
    merchantUser
  ) - Perms.USER_READ_ME

  val merchantSecretKeyWithSiteContext = PermSetGroup.groups(
    merchantUserWithSiteContext
  ) - Perms.USER_READ_ME

  val user = PermSetGroup.sets(
    PermSets.apiKeyRead,
    PermSets.apiKeyModify,
    PermSets.authRead,
    PermSets.invoiceSearch,
    PermSets.merchantModify,
    PermSets.merchantSearch,
    PermSets.providerRead,
    PermSets.siteModify,
    PermSets.siteSearch,
    PermSets.userReadMe,
    PermSets.userRead,
    PermSets.userModify
  )

  val providerUserWithMerchantContext = PermSetGroup.groups(
    user,
    merchantUser
  ) - Perms.MERCHANT_CREATE

  val providerUserWithSiteContext = PermSetGroup.groups(
    user,
    siteUser
  ) - Perms.MERCHANT_CREATE - Perms.SITE_CREATE

  val providerPublishableKeyWithMerchantContext = PermSetGroup.groups(
    merchantPublishableKey
  )

  val providerPublishableKeyWithSiteContext = PermSetGroup.groups(
    merchantPublishableKeyWithSiteContext
  )

  val providerSecretKey = PermSetGroup.groups(
    user
  )

  val providerSecretKeyWithMerchantContext = PermSetGroup.groups(
    providerUserWithMerchantContext
  )

  val providerSecretKeyWithSiteContext = PermSetGroup.groups(
    providerUserWithSiteContext
  )

  val god = PermSetGroup.sets(
    PermSets.apiKeyRead,
    PermSets.apiKeyModify,
    PermSets.authRead,
    PermSets.invoiceModify,
    PermSets.invoiceUpdate,
    PermSets.invoiceRead,
    PermSets.invoiceSearch,
    PermSets.ipn,
    PermSets.merchantModify,
    PermSets.merchantRead,
    PermSets.merchantSearch,
    PermSets.merchantUpdate,
    PermSets.providerRead,
    PermSets.siteModify,
    PermSets.siteRead,
    PermSets.siteSearch,
    PermSets.siteUpdate,
    PermSets.userReadMe,
    PermSets.userRead,
    PermSets.userModify)

}

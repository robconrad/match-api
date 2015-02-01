/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 4:23 PM
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
      LOGIN))
  }

  test("user") {
    assertGroup(user, Set(
      REGISTER_PHONE,
      VERIFY_PHONE,
      INVITE,
      ACCEPT_INVITE,
      DECLINE_INVITE,
      MESSAGE,
      ANSWER,
      QUESTIONS))
  }

}

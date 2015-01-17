/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/16/15 11:07 PM
 */

package base.entity.perm

/**
 * Collections of PermSets that are granted to particular profiles of AuthContexts.
 * @author rconrad
 */
object PermSetGroups {

  val none = PermSetGroup(PermSet())

  val public = PermSetGroup.sets(
    PermSets.register,
    PermSets.verify,
    PermSets.login
  )

  val user = PermSetGroup.sets(
    PermSets.invite,
    PermSets.message,
    PermSets.answer,
    PermSets.questions
  )

}

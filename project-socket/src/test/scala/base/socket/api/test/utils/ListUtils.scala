/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 1:01 PM
 */

package base.socket.api.test.utils

import base.entity.group.model.GroupModel
import base.entity.question.model.QuestionModel
import base.entity.user.model.UserModel

/**
 * {{ Describe the high level purpose of ListUtils here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object ListUtils {

  def sortUsers(users: List[UserModel]) = users.sortBy(_.id.toString)

  def sortGroups(groups: List[GroupModel]) = groups.sortBy(_.id.toString)

  def sortQuestions(questions: List[QuestionModel]) = questions.sortBy(q => q.id.toString + q.side)

}

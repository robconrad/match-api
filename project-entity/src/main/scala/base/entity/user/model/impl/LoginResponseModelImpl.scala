/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:24 PM
 */

package base.entity.user.model.impl

import base.entity.event.model.EventModel
import base.entity.group.model.GroupModel
import base.entity.question.model.QuestionModel
import base.entity.user.model.{ LoginResponseModel, UserModel }
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of LoginResponseModelImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class LoginResponseModelImpl(user: UserModel,
                                  phone: Option[String],
                                  phoneVerified: Boolean,
                                  pendingGroups: List[GroupModel],
                                  groups: List[GroupModel],
                                  events: Option[List[EventModel]],
                                  questions: Option[List[QuestionModel]],
                                  lastLoginTime: Option[DateTime])
    extends LoginResponseModel

case class LoginResponseModelBuilder(user: Option[UserModel] = None,
                                     phone: Option[Option[String]] = None,
                                     phoneVerified: Option[Boolean] = None,
                                     pendingGroups: Option[List[GroupModel]] = None,
                                     groups: Option[List[GroupModel]] = None,
                                     events: Option[Option[List[EventModel]]] = None,
                                     questions: Option[Option[List[QuestionModel]]] = None,
                                     lastLoginTime: Option[Option[DateTime]] = None) {

  def build = LoginResponseModelImpl(
    user.get,
    phone.get,
    phoneVerified.get,
    pendingGroups.get,
    groups.get,
    events.get,
    questions.get,
    lastLoginTime.get)

}

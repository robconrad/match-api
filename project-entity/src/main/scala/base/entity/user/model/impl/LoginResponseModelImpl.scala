/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 3:52 PM
 */

package base.entity.user.model.impl

import base.entity.event.model.EventModel
import base.entity.group.model.{ GroupModel, InviteModel }
import base.entity.question.model.QuestionModel
import base.entity.user.model.{ UserModel, LoginResponseModel }
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
                                  pendingInvites: List[InviteModel],
                                  pendingGroups: List[GroupModel],
                                  groups: List[GroupModel],
                                  events: Option[List[EventModel]],
                                  questions: Option[List[QuestionModel]],
                                  lastLoginTime: Option[DateTime])
    extends LoginResponseModel

case class LoginResponseModelBuilder(user: Option[UserModel] = None,
                                     phone: Option[String] = None,
                                     phoneVerified: Option[Boolean] = None,
                                     pendingInvites: Option[List[InviteModel]] = None,
                                     pendingGroups: Option[List[GroupModel]] = None,
                                     groups: Option[List[GroupModel]] = None,
                                     events: Option[List[EventModel]] = None,
                                     questions: Option[List[QuestionModel]] = None,
                                     lastLoginTime: Option[DateTime] = None) {

  def build = LoginResponseModelImpl(
    user.get,
    phone,
    phoneVerified.get,
    pendingInvites.get,
    pendingGroups.get,
    groups.get,
    events,
    questions,
    lastLoginTime)

}

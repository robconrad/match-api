/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:40 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.group.kv._
import base.entity.group.kv.impl._
import base.entity.kv.ScredisKeyFactoryService
import base.entity.question.kv.QuestionsKey
import base.entity.question.kv.impl.QuestionsKeyImpl
import base.entity.user.kv.impl.{ PhoneGroupsInvitedKeyImpl, UserGroupsInvitedKeyImpl, UserGroupsKeyImpl, UserPhonesInvitedKeyImpl }
import base.entity.user.kv.{ PhoneGroupsInvitedKey, UserGroupsInvitedKey, UserGroupsKey, UserPhonesInvitedKey }

/**
 * {{ Describe the high level purpose of KeyFactoryServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ScredisKeyFactoryServiceImpl extends ServiceImpl with ScredisKeyFactoryService {

  // format: OFF
  // scalastyle:off line.size.limit
  private val registry = Map[Manifest[_], Any => Any](
    manifest[GroupPhonesInvitedKey]         -> ((id: Any) => new GroupPhonesInvitedKeyImpl(id.asInstanceOf[UUID])),
    manifest[GroupUserQuestionsKey]         -> ((id: Any) => new GroupUserQuestionsKeyImpl(id.asInstanceOf[(UUID, UUID)])),
    manifest[GroupUserQuestionsTempKey]     -> ((id: Any) => new GroupUserQuestionsTempKeyImpl(id.asInstanceOf[(UUID, UUID)])),
    manifest[GroupUserQuestionsYesKey]      -> ((id: Any) => new GroupUserQuestionsYesKeyImpl(id.asInstanceOf[(UUID, UUID)])),
    manifest[GroupUsersKey]                 -> ((id: Any) => new GroupUsersKeyImpl(id.asInstanceOf[UUID])),
    manifest[PhoneGroupsInvitedKey]         -> ((id: Any) => new PhoneGroupsInvitedKeyImpl(id.asInstanceOf[String])),
    manifest[QuestionsKey]                  -> ((id: Any) => new QuestionsKeyImpl(id.asInstanceOf[String])),
    manifest[UserGroupsInvitedKey]          -> ((id: Any) => new UserGroupsInvitedKeyImpl(id.asInstanceOf[UUID])),
    manifest[UserPhonesInvitedKey]          -> ((id: Any) => new UserPhonesInvitedKeyImpl(id.asInstanceOf[UUID])),
    manifest[UserGroupsKey]                 -> ((id: Any) => new UserGroupsKeyImpl(id.asInstanceOf[UUID]))
  )
  // format: ON

  def make[T](id: Any)(implicit m: Manifest[T]) = registry(m)(id).asInstanceOf[T]

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:56 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.facebook.kv.FacebookInfoKey
import base.entity.facebook.kv.impl.FacebookInfoKeyImpl
import base.entity.group.kv._
import base.entity.group.kv.impl._
import base.entity.kv.ScredisKeyFactoryService
import base.entity.question.kv.impl.{QuestionKeyImpl, QuestionsKeyImpl}
import base.entity.question.kv.{QuestionKey, QuestionsKey}
import base.entity.user.kv._
import base.entity.user.kv.impl._

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
    manifest[DeviceKey]                     -> ((id: Any) => new DeviceKeyImpl(id.asInstanceOf[UUID])),
    manifest[FacebookInfoKey]               -> ((id: Any) => new FacebookInfoKeyImpl(id.asInstanceOf[String])),
    manifest[FacebookUserKey]               -> ((id: Any) => new FacebookUserKeyImpl(id.asInstanceOf[String])),
    manifest[GroupKey]                      -> ((id: Any) => new GroupKeyImpl(id.asInstanceOf[UUID])),
    manifest[GroupUserKey]                  -> ((id: Any) => new GroupUserKeyImpl(id.asInstanceOf[(UUID, UUID)])),
    manifest[QuestionKey]                   -> ((id: Any) => new QuestionKeyImpl(id.asInstanceOf[UUID])),
    manifest[UserKey]                       -> ((id: Any) => new UserKeyImpl(id.asInstanceOf[UUID])),
    manifest[GroupPhonesInvitedKey]         -> ((id: Any) => new GroupPhonesInvitedKeyImpl(id.asInstanceOf[UUID])),
    manifest[GroupQuestionsKey]             -> ((id: Any) => new GroupQuestionsKeyImpl(id.asInstanceOf[UUID])),
    manifest[GroupUserQuestionsKey]         -> ((id: Any) => new GroupUserQuestionsKeyImpl(id.asInstanceOf[(UUID, UUID)])),
    manifest[GroupUserQuestionsTempKey]     -> ((id: Any) => new GroupUserQuestionsTempKeyImpl(id.asInstanceOf[(UUID, UUID)])),
    manifest[GroupUserQuestionsYesKey]      -> ((id: Any) => new GroupUserQuestionsYesKeyImpl(id.asInstanceOf[(UUID, UUID)])),
    manifest[GroupUsersKey]                 -> ((id: Any) => new GroupUsersKeyImpl(id.asInstanceOf[UUID])),
    manifest[PhoneKey]                      -> ((id: Any) => new PhoneKeyImpl(id.asInstanceOf[String])),
    manifest[PhoneCooldownKey]              -> ((id: Any) => new PhoneCooldownKeyImpl(id.asInstanceOf[String])),
    manifest[PhoneGroupsInvitedKey]         -> ((id: Any) => new PhoneGroupsInvitedKeyImpl(id.asInstanceOf[String])),
    manifest[QuestionKey]                   -> ((id: Any) => new QuestionKeyImpl(id.asInstanceOf[UUID])),
    manifest[QuestionsKey]                  -> ((id: Any) => new QuestionsKeyImpl(id.asInstanceOf[String])),
    manifest[UserGroupsInvitedKey]          -> ((id: Any) => new UserGroupsInvitedKeyImpl(id.asInstanceOf[UUID])),
    manifest[UserGroupsKey]                 -> ((id: Any) => new UserGroupsKeyImpl(id.asInstanceOf[UUID])),
    manifest[UserPhonesInvitedKey]          -> ((id: Any) => new UserPhonesInvitedKeyImpl(id.asInstanceOf[UUID])),
    manifest[UserPhoneLabelKey]             -> ((id: Any) => new UserPhoneLabelKeyImpl(id.asInstanceOf[UserPhone])),
    manifest[UserQuestionsKey]              -> ((id: Any) => new UserQuestionsKeyImpl(id.asInstanceOf[UUID]))
  )
  // format: ON

  def make[T](id: Any)(implicit m: Manifest[T]) = registry(m)(id).asInstanceOf[T]

}

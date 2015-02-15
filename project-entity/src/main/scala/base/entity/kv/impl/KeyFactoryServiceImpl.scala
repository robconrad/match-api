/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 12:10 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.event.model.EventModel
import base.entity.facebook.FacebookInfo
import base.entity.facebook.kv.FacebookInfoKey
import base.entity.group.kv._
import base.entity.group.kv.impl._
import base.entity.kv.KeyPrefixes._
import base.entity.kv.serializer.SerializerImplicits._
import base.entity.kv.{ KeyPrefixes, OrderedIdPair, KeyCommandsService, KeyFactoryService }
import base.entity.question.QuestionIdComposite
import base.entity.question.kv.impl.QuestionKeyImpl
import base.entity.question.kv.{ QuestionsKey, QuestionKey }
import base.entity.user.kv._
import base.entity.user.kv.impl._
import scredis.Client
import scredis.keys.HashKeyProps
import scredis.keys.impl.{ SetKeyImpl, ListKeyImpl, SimpleKeyImpl }
import scredis.serialization.Writer

/**
 * {{ Describe the high level purpose of KeyFactoryServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class KeyFactoryServiceImpl extends ServiceImpl with KeyFactoryService {

  type KP = Short
  type OIP = OrderedIdPair
  type QIC = QuestionIdComposite

  private implicit def client: Client = KeyCommandsService().client

  private implicit def any2String(id: Any): String = id.asInstanceOf[String]
  private implicit def any2UUID(id: Any): UUID = id.asInstanceOf[UUID]
  private implicit def any2UserPhone(id: Any): UserPhone = id.asInstanceOf[UserPhone]
  private implicit def any2OrderedIdPair(id: Any): OrderedIdPair =
    OrderedIdPair(id.asInstanceOf[(UUID, UUID)]._1, id.asInstanceOf[(UUID, UUID)]._2)

  private def makeHashKey[T](keyPrefix: KeyPrefix, id: T)(implicit w: Writer[T]) = {
    (props: HashKeyProps) =>
      {
        implicit val p = props
        new scredis.keys.impl.HashKeyImpl[KP, T](keyPrefix, id)
      }
  }

  def make[T](id: Any)(implicit m: Manifest[T]) = registry(m)(id).asInstanceOf[T]

  // format: OFF
  // scalastyle:off line.size.limit
  private val registry = Map[Manifest[_], Any => Any](
    // hash keys
    manifest[DeviceKey]                     -> ((id: Any) => new DeviceKeyImpl    (makeHashKey            (device, id))),
    manifest[GroupKey]                      -> ((id: Any) => new GroupKeyImpl     (makeHashKey            (group, id))),
    manifest[GroupUserKey]                  -> ((id: Any) => new GroupUserKeyImpl (makeHashKey            (groupUser, id))),
    manifest[QuestionKey]                   -> ((id: Any) => new QuestionKeyImpl  (makeHashKey            (question, id))),
    manifest[UserKey]                       -> ((id: Any) => new UserKeyImpl      (makeHashKey            (user, id))),
    // list keys
    manifest[GroupEventsKey]                -> ((id: Any) => new ListKeyImpl[KP, UUID, EventModel]        (groupEvents, id)             with GroupEventsKey),
    // set keys
    manifest[GroupPhonesInvitedKey]         -> ((id: Any) => new SetKeyImpl[KP, UUID, String]             (groupPhonesInvited, id)      with GroupPhonesInvitedKey),
    manifest[GroupQuestionsKey]             -> ((id: Any) => new SetKeyImpl[KP, UUID, QIC]                (groupQuestions, id)          with GroupQuestionsKey),
    manifest[GroupUserQuestionsKey]         -> ((id: Any) => new SetKeyImpl[KP, OIP, QIC]                 (groupUserQuestions, id)      with GroupUserQuestionsKey),
    manifest[GroupUserQuestionsTempKey]     -> ((id: Any) => new SetKeyImpl[KP, OIP, QIC]                 (groupUserQuestionsTemp, id)  with GroupUserQuestionsTempKey),
    manifest[GroupUserQuestionsYesKey]      -> ((id: Any) => new SetKeyImpl[KP, OIP, QIC]                 (groupUserQuestionsYes, id)   with GroupUserQuestionsYesKey),
    manifest[GroupUsersKey]                 -> ((id: Any) => new SetKeyImpl[KP, UUID, UUID]               (groupUsers, id)              with GroupUsersKey),
    manifest[PhoneGroupsInvitedKey]         -> ((id: Any) => new SetKeyImpl[KP, String, UUID]             (phoneGroupsInvited, id)      with PhoneGroupsInvitedKey),
    manifest[QuestionsKey]                  -> ((id: Any) => new SetKeyImpl[KP, String, QIC]              (questions, id)               with QuestionsKey),
    manifest[UserGroupsInvitedKey]          -> ((id: Any) => new SetKeyImpl[KP, UUID, UUID]               (userGroupsInvited, id)       with UserGroupsInvitedKey),
    manifest[UserGroupsKey]                 -> ((id: Any) => new SetKeyImpl[KP, UUID, UUID]               (userGroups, id)              with UserGroupsKey),
    manifest[UserPhonesInvitedKey]          -> ((id: Any) => new SetKeyImpl[KP, UUID, String]             (userPhonesInvited, id)       with UserPhonesInvitedKey),
    manifest[UserQuestionsKey]              -> ((id: Any) => new SetKeyImpl[KP, UUID, QIC]                (userQuestions, id)           with UserQuestionsKey),
    // simple keys
    manifest[FacebookInfoKey]               -> ((id: Any) => new SimpleKeyImpl[KP, String, FacebookInfo]  (facebookInfo, id)            with FacebookInfoKey),
    manifest[FacebookUserKey]               -> ((id: Any) => new SimpleKeyImpl[KP, String, UUID]          (facebookUser, id)            with FacebookUserKey),
    manifest[PhoneCooldownKey]              -> ((id: Any) => new SimpleKeyImpl[KP, String, Boolean]       (phoneCooldown, id)           with PhoneCooldownKey),
    manifest[PhoneKey]                      -> ((id: Any) => new SimpleKeyImpl[KP, String, UUID]          (phone, id)                   with PhoneKey),
    manifest[UserPhoneLabelKey]             -> ((id: Any) => new SimpleKeyImpl[KP, UserPhone, String]     (userPhoneLabel, id)          with UserPhoneLabelKey)
  )
  // format: ON

  // the -1 is to account for the test prefix
  assert(registry.keys.size == KeyPrefixes.values.size - 1)

}

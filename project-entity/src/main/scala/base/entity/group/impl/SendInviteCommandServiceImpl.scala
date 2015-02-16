/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 7:01 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.{StandardUserAuthContext, ChannelContext}
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.EventTypes
import base.entity.event.model.impl.EventModelImpl
import base.entity.group._
import base.entity.group.impl.SendInviteCommandServiceImpl.Errors
import base.entity.group.kv._
import base.entity.group.model.{ GroupModel, SendInviteModel, SendInviteResponseModel }
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel
import base.entity.service.CrudErrorImplicits
import base.entity.user.kv._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class SendInviteCommandServiceImpl(welcomeMessage: String)
    extends CommandServiceImpl[SendInviteModel, SendInviteResponseModel]
    with SendInviteCommandService {

  override protected val responseManifest = Option(manifest[SendInviteResponseModel])

  def innerExecute(input: SendInviteModel)(implicit channelCtx: ChannelContext) = {
    new InviteCommand(input).execute()
  }

  /**
   * - add phone to invited set
   * - create invited phone label
   * - create group
   * - get invited userId from phone
   * - add invite to phone if no user id
   * - add invite to user if yes user id
   * - add inviter to group users
   * - add group to inviter groups
   * - add invited phone to group phones invited
   * - add welcome message to group events
   * - register group listener
   * - get and return group, events, questions
   */
  private[impl] class InviteCommand(val input: SendInviteModel)(implicit val channelCtx: ChannelContext)
      extends Command[SendInviteModel, SendInviteResponseModel] {

    def execute() = {
      userPhonesInvitedAdd(make[UserPhonesInvitedKey](authCtx.userId))
    }

    def userPhonesInvitedAdd(key: UserPhonesInvitedKey) =
      key.add(input.phone) flatMap {
        case 1L => userPhoneLabelSet(make[UserPhoneLabelKey](UserPhone(authCtx.userId, input.phone)))
        case 0L => Errors.alreadyInvited
        case _  => Errors.userInvitedPhonesSetFailed
      }

    def userPhoneLabelSet(key: UserPhoneLabelKey) =
      key.set(input.label) flatMap {
        case true =>
          val groupId = RandomService().uuid
          groupCreate(groupId, make[GroupKey](groupId))
        case false => Errors.userPhoneLabelSetFailed
      }

    def groupCreate(groupId: UUID, key: GroupKey) =
      key.create() flatMap { result =>
        phoneGetUserId(groupId, make[PhoneKey](input.phone))
      }

    def phoneGetUserId(groupId: UUID, key: PhoneKey) =
      key.get.flatMap {
        case Some(userId) if userId == authCtx.userId =>
          Errors.selfInvited
        case Some(userId) =>
          userGroupsInvitedAdd(groupId, make[UserGroupsInvitedKey](userId))
        case None =>
          phoneGroupsInvitedAdd(groupId, make[PhoneGroupsInvitedKey](input.phone))
      }

    def phoneGroupsInvitedAdd(groupId: UUID, key: PhoneGroupsInvitedKey) =
      key.add(groupId) flatMap {
        case 1L => groupUsersAdd(groupId, make[GroupUsersKey](groupId))
        case _  => Errors.phoneGroupsInvitedAddFailed
      }

    def userGroupsInvitedAdd(groupId: UUID, key: UserGroupsInvitedKey) =
      key.add(groupId) flatMap {
        case 1L => groupUsersAdd(groupId, make[GroupUsersKey](groupId))
        case _  => Errors.userGroupsInvitedAddFailed
      }

    def groupUsersAdd(groupId: UUID, key: GroupUsersKey) =
      key.add(authCtx.userId).flatMap {
        case 1L => userGroupsAdd(groupId, make[UserGroupsKey](authCtx.userId))
        case _  => Errors.groupUsersAddFailed
      }

    def userGroupsAdd(groupId: UUID, key: UserGroupsKey) =
      key.add(groupId).flatMap {
        case 1L => groupPhonesInvitedAdd(groupId, make[GroupPhonesInvitedKey](groupId))
        case _  => Errors.userGroupsAddFailed
      }

    def groupPhonesInvitedAdd(groupId: UUID, key: GroupPhonesInvitedKey) =
      key.add(input.phone).flatMap {
        case 1L => groupEventsPrepend(groupId)
        case _  => Errors.groupPhonesInvitedAddFailed
      }

    def groupEventsPrepend(groupId: UUID) = {
      val event = EventModelImpl(
        id = RandomService().uuid,
        groupId = groupId,
        `type` = EventTypes.MESSAGE,
        body = welcomeMessage)
      GroupEventsService().setEvent(event, createIfNotExists = true).flatMap {
        case Right(event) => registerGroupListener(groupId)
        case Left(error)  => error
      }
    }

    def registerGroupListener(groupId: UUID) =
      GroupListenerService().register(authCtx.userId, Set(groupId)).flatMap { x =>
        groupGet(groupId)
      }

    def groupGet(groupId: UUID) =
      GroupService().getGroup(authCtx.userId, groupId).flatMap {
        case Right(Some(group)) => questionsGet(groupId, group)
        case Right(None)        => Errors.groupGetFailed
        case Left(error)        => error
      }

    def questionsGet(groupId: UUID, group: GroupModel) =
      QuestionService().getQuestions(groupId, authCtx.userId).flatMap {
        case Right(questions) => eventsGet(groupId, group, questions)
        case Left(error)      => error
      }

    def eventsGet(groupId: UUID, group: GroupModel, questions: List[QuestionModel]) =
      GroupEventsService().getEvents(groupId).flatMap {
        case Left(error)   => error
        case Right(events) =>
          channelCtx.authCtx = StandardUserAuthContext(authCtx.userThrows, authCtx.groups + group.id)
          SendInviteResponseModel(group, events, questions)
      }

  }

}

object SendInviteCommandServiceImpl {

  object Errors extends CrudErrorImplicits[SendInviteResponseModel] {

    override protected val externalErrorText = "There was a problem during invite."

    lazy val alreadyInvited: Response = ("The supplied phone number has already been invited.", INVITED_ALREADY)
    lazy val selfInvited: Response = ("The supplied phone number belongs to this user.", INVITED_SELF)
    lazy val userInvitedPhonesSetFailed: Response = "failed to add phone to user invited set"
    lazy val userGroupsInvitedAddFailed: Response = "failed to add group to user invites"
    lazy val phoneGroupsInvitedAddFailed: Response = "failed to add group to phone invites"
    lazy val userPhoneLabelSetFailed: Response = "failed to set userUserLabel"
    lazy val groupGetFailed: Response = "failed to get group"
    lazy val groupCreateFailed: Response = "failed to create group"
    lazy val groupUsersAddFailed: Response = "failed to add to groupUsers"
    lazy val groupPhonesInvitedAddFailed: Response = "failed to add to groupPhonesInvited"
    lazy val userGroupsAddFailed: Response = "failed to add to userGroups"

  }

}

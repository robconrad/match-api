/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 11:56 AM
 */

package base.entity.group.impl

import base.common.random.RandomService
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.EventTypes
import base.entity.event.model.impl.EventModelImpl
import base.entity.group._
import base.entity.group.impl.AcceptInviteCommandServiceImpl.Errors
import base.entity.group.kv.{ GroupUsersKey, GroupUsersKeyService }
import base.entity.group.model.{ AcceptInviteModel, GroupModel, InviteResponseModel }
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel
import base.entity.service.CrudErrorImplicits
import base.entity.user.kv.{ UserGroupsInvitedKeyService, UserGroupsInvitedKey, UserGroupsKey, UserGroupsKeyService }

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class AcceptInviteCommandServiceImpl(joinMessage: String)
    extends CommandServiceImpl[AcceptInviteModel, InviteResponseModel]
    with AcceptInviteCommandService {

  override protected val responseManifest = Option(manifest[InviteResponseModel])

  def innerExecute(input: AcceptInviteModel)(implicit channelCtx: ChannelContext) = {
    new AcceptInviteCommand(input).execute()
  }

  /**
   * - remove invite
   * - add invitee to group users
   * - add group to invitee groups
   * - add join message to group events
   * - register group listener
   * - get and return group, events, questions
   */
  private[impl] class AcceptInviteCommand(val input: AcceptInviteModel)(implicit val channelCtx: ChannelContext)
      extends Command[AcceptInviteModel, InviteResponseModel] {

    def execute(): Response = {
      userGroupsInvitedRemove(UserGroupsInvitedKeyService().make(authCtx.userId))
    }

    def userGroupsInvitedRemove(key: UserGroupsInvitedKey): Response =
      key.remove(input.groupId) flatMap {
        case 1L => groupUsersAdd(GroupUsersKeyService().make(input.groupId))
        case _  => Errors.userGroupsInvitedRemoveFailed
      }

    def groupUsersAdd(key: GroupUsersKey): Response =
      key.add(authCtx.userId).flatMap {
        case 1L => userGroupsAdd(UserGroupsKeyService().make(authCtx.userId))
        case _  => Errors.groupUsersAddFailed
      }

    def userGroupsAdd(key: UserGroupsKey): Response =
      key.add(input.groupId).flatMap {
        case 1L => groupEventsPrepend()
        case _  => Errors.userGroupsAddFailed
      }

    def groupEventsPrepend(): Response = {
      val event = EventModelImpl(
        id = RandomService().uuid,
        groupId = input.groupId,
        userId = Option(authCtx.userId),
        `type` = EventTypes.JOIN,
        body = joinMessage)
      GroupEventsService().setEvent(event, createIfNotExists = true).flatMap {
        case Right(event) => registerGroupListener()
        case Left(error)  => error
      }
    }

    def registerGroupListener(): Response =
      GroupListenerService().register(authCtx.userId, Set(input.groupId)).flatMap { x =>
        groupGet()
      }

    def groupGet(): Response =
      GroupService().getGroup(authCtx.userId, input.groupId).flatMap {
        case Right(Some(group)) => questionsGet(group)
        case Right(None)        => Errors.groupGetFailed
        case Left(error)        => error
      }

    def questionsGet(group: GroupModel): Response =
      QuestionService().getQuestions(input.groupId, authCtx.userId).flatMap {
        case Right(questions) => eventsGet(group, questions)
        case Left(error)      => error
      }

    def eventsGet(group: GroupModel, questions: List[QuestionModel]): Response =
      GroupEventsService().getEvents(input.groupId).flatMap {
        case Right(events) => InviteResponseModel(group, events, questions)
        case Left(error)   => error
      }

  }

}

object AcceptInviteCommandServiceImpl {

  object Errors extends CrudErrorImplicits[InviteResponseModel] {

    override protected val externalErrorText = "There was a problem during accept invite."

    lazy val userGroupsInvitedRemoveFailed: Response = "failed to remove invite from user groups invited"
    lazy val groupUsersAddFailed: Response = "failed to add to groupUsers"
    lazy val userGroupsAddFailed: Response = "failed to add to userGroups"
    lazy val groupGetFailed: Response = "failed to get group"

  }

}

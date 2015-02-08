/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 12:13 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.facebook.{ FacebookInfo, FacebookService }
import base.entity.group.{ GroupEventsService, GroupListenerService }
import base.entity.question.QuestionService
import base.entity.service.CrudErrorImplicits
import base.entity.user._
import base.entity.user.impl.LoginCommandServiceImpl.Errors
import base.entity.user.kv._
import base.entity.user.model.impl.LoginResponseModelBuilder
import base.entity.user.model.{ LoginModel, LoginResponseModel, UserModel }
import spray.http.StatusCodes._

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class LoginCommandServiceImpl()
    extends CommandServiceImpl[LoginModel, LoginResponseModel]
    with LoginCommandService {

  override protected val responseManifest = Option(manifest[LoginResponseModel])

  def innerExecute(input: LoginModel)(implicit channelCtx: ChannelContext) = {
    new LoginCommand(input).execute()
  }

  /**
   * - get fbInfo from token
   * - get userId from fbId
   * - store fbInfo on user
   * - update device attributes
   * - retrieve groups
   * - optionally retrieve current group events
   * - optionally retrieve current group questions
   * - update user attributes (last login)
   */
  private[impl] class LoginCommand(val input: LoginModel)(implicit val channelCtx: ChannelContext)
      extends Command[LoginModel, LoginResponseModel] {

    def execute(): Response = {
      facebookInfoGet()
    }

    def facebookInfoGet(): Response = {
      FacebookService().getInfo(input.fbToken) flatMap {
        case Some(fbInfo) => facebookUserGet(FacebookUserKeyService() make fbInfo.id, fbInfo)
        case None         => Errors.tokenInvalid
      }
    }

    def facebookUserGet(key: FacebookUserKey, fbInfo: FacebookInfo): Response = {
      key.get flatMap {
        case Some(userId) => userSet(UserKeyService().make(userId), userId, fbInfo)
        case None         => facebookUserSet(key, RandomService().uuid, fbInfo)
      }
    }

    def facebookUserSet(key: FacebookUserKey, userId: UUID, fbInfo: FacebookInfo): Response = {
      key.set(userId) flatMap {
        case true  => userSet(UserKeyService().make(userId), userId, fbInfo)
        case false => Errors.facebookUserSetFailed
      }
    }

    def userSet(key: UserKey, userId: UUID, fbInfo: FacebookInfo): Response = {
      key.setFacebookInfo(fbInfo) flatMap {
        case true  => deviceSet(DeviceKeyService() make input.device.uuid, userId)
        case false => Errors.userSetFailed
      }
    }

    def deviceSet(key: DeviceKey, userId: UUID): Response = {
      key.set(
        input.appVersion,
        input.locale,
        input.device.model,
        input.device.cordova,
        input.device.platform,
        input.device.version
      ).flatMap {
          case true  => groupsGet(userId)
          case false => Errors.deviceSetFailed
        }
    }

    def groupsGet(userId: UUID): Response = {
      val key = UserKeyService().make(userId)
      UserService().getGroups(userId).flatMap {
        case Left(error) => error
        case Right(groups) =>
          val builder = LoginResponseModelBuilder(groups = Option(groups))
          input.groupId match {
            case Some(groupId) => eventsGet(key, userId, groupId, builder)
            case None =>
              userGetLoginAttributes(key, userId, builder.copy(events = Option(None), questions = Option(None)))
          }
      }
    }

    def eventsGet(key: UserKey, userId: UUID, groupId: UUID, builder: LoginResponseModelBuilder): Response = {
      GroupEventsService().getEvents(groupId).flatMap {
        case Left(error) => error
        case Right(events) =>
          QuestionService().getQuestions(groupId, userId).flatMap {
            case Left(error) => error
            case Right(questions) =>
              userGetLoginAttributes(key, userId, builder.copy(
                events = Option(Option(events)),
                questions = Option(Option(questions))))
          }
      }
    }

    def userGetLoginAttributes(key: UserKey, userId: UUID, builder: LoginResponseModelBuilder): Response = {
      key.getLoginAttributes flatMap { attributes =>
        userGetPendingGroups(UserService(), userId, builder.copy(
          phone = Option(attributes.phone),
          phoneVerified = Option(attributes.phoneVerified),
          user = Option(UserModel(userId, attributes.name.pictureUrl, attributes.name.name)),
          lastLoginTime = Option(attributes.lastLogin)))
      }
    }

    def userGetPendingGroups(service: UserService, userId: UUID, builder: LoginResponseModelBuilder): Response = {
      service.getPendingGroups(userId) flatMap {
        case Right(groups) => setLastLogin(UserKeyService().make(userId), builder.copy(pendingGroups = Option(groups)))
        case Left(error)   => error
      }
    }

    def setLastLogin(key: UserKey, builder: LoginResponseModelBuilder): Response = {
      key.setLastLogin() flatMap {
        case true  => registerGroupListeners(builder.build)
        case false => Errors.userSetFailed
      }
    }

    def registerGroupListeners(response: LoginResponseModel): Response = {
      GroupListenerService().register(response.user.id, response.groups.map(_.id).toSet) map { x =>
        response
      }
    }

  }

}

object LoginCommandServiceImpl {

  object Errors extends CrudErrorImplicits[LoginResponseModel] {

    override protected val externalErrorText = "There was a problem during login."

    private val tokenInvalidText = "The supplied Facebook token is not valid."

    lazy val tokenInvalid: Response = (tokenInvalidText, Unauthorized, TOKEN_INVALID)
    lazy val deviceSetFailed: Response = "failed to set device attributes"
    lazy val facebookUserSetFailed: Response = "failed to set facebook user"
    lazy val userSetFailed: Response = "failed to set user attributes"

  }

}

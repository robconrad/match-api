/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/17/15 10:34 PM
 */

package base.socket.command.impl

import base.common.lib.Tryo
import base.common.logging.Loggable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.{ AuthContext, ChannelContext, StandardUserAuthContext }
import base.entity.command.CommandNames
import base.entity.command.CommandNames.CommandName
import base.entity.command.model.CommandModel
import base.entity.error.ApiErrorService
import base.entity.error.model.ApiError
import base.entity.event.AckEventsCommandService
import base.entity.event.model.AckEventsModel
import base.entity.group.model.{ AcceptInviteModel, DeclineInviteModel, SendInviteModel }
import base.entity.group.{ AcceptInviteCommandService, DeclineInviteCommandService, SendInviteCommandService }
import base.entity.json.JsonFormats
import base.entity.logging.AuthLoggable
import base.entity.message.MessageCommandService
import base.entity.message.model.MessageModel
import base.entity.question.model.{ AnswerModel, CreateQuestionModel, QuestionsModel }
import base.entity.question.{ AnswerCommandService, CreateQuestionCommandService, QuestionsCommandService }
import base.entity.user.model.{ LoginModel, LoginResponseModel, RegisterPhoneModel, VerifyPhoneModel }
import base.entity.user.{ LoginCommandService, RegisterPhoneCommandService, User, VerifyPhoneCommandService }
import base.socket.command.CommandProcessingService
import base.socket.command.CommandProcessingService.{ CommandProcessError, CommandProcessResult, FutureResponse, Response }
import base.socket.command.impl.CommandProcessingServiceImpl.Errors
import org.json4s.JValue
import org.json4s.JsonAST.{ JObject, JString }
import org.json4s.native.{ JsonMethods, Serialization }
import spray.http.StatusCodes

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of CommandServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CommandProcessingServiceImpl extends ServiceImpl with CommandProcessingService with AuthLoggable {

  implicit val formats = JsonFormats.withModels

  implicit def response2Future(r: Response): Future[Response] = Future.successful(r)

  def process(input: String)(implicit channelCtx: ChannelContext) = {
    new ProcessCommand().parseInput(input)
  }

  private[impl] class ProcessCommand()(implicit channelCtx: ChannelContext) {

    private lazy val alreadyLoggedInError =
      Future.successful(returnError(ALREADY_LOGGED_IN, "this socket is already authenticated"))

    def parseInput(input: String): FutureResponse = {
      try {
        extractCommand(JsonMethods.parse(input)) recover {
          case t => returnError("parse threw exception %s", Loggable.stackTraceToString(t))
        }
      } catch {
        case e: Exception => returnError("parse threw exception %s", Loggable.stackTraceToString(e))
      }
    }

    // expecting { "cmd": "$name", "body": { $body } }
    def extractCommand(json: JValue): FutureResponse = {
      json match {
        case json: JObject =>
          json \ "cmd" match {
            case cmd: JString =>
              CommandNames.findByName(cmd.s) match {
                case Some(cmd) =>
                  json \ "body" match {
                    case body: JObject => processCommand(cmd, body)
                    case _             => returnError(JSON_BODY_NOT_FOUND, "unable to parse body from msg: %s", json)
                  }
                case None => returnError(JSON_COMMAND_NOT_FOUND, "unable to match cmd from msg: %s", json)
              }
            case _ => returnError(JSON_COMMAND_NOT_FOUND, "unable to parse cmd from msg: %s", json)
          }
        case _ => returnError(JSON_NOT_FOUND, "no json received in msg: %s", json)
      }
    }

    // scalastyle:off return
    // scalastyle:off line.size.limit
    def processCommand(cmd: CommandName, body: JObject): FutureResponse = {
      val response: Future[Option[_]] = cmd match {
        case CommandNames.login if authCtx.hasUser => return alreadyLoggedInError
        case CommandNames.login                    => LoginCommandService().execute(body.extract[LoginModel])
        case CommandNames.registerPhone            => RegisterPhoneCommandService().execute(body.extract[RegisterPhoneModel])
        case CommandNames.verifyPhone              => VerifyPhoneCommandService().execute(body.extract[VerifyPhoneModel])
        case CommandNames.sendInvite               => SendInviteCommandService().execute(body.extract[SendInviteModel])
        case CommandNames.acceptInvite             => AcceptInviteCommandService().execute(body.extract[AcceptInviteModel])
        case CommandNames.declineInvite            => DeclineInviteCommandService().execute(body.extract[DeclineInviteModel])
        case CommandNames.questions                => QuestionsCommandService().execute(body.extract[QuestionsModel])
        case CommandNames.message                  => MessageCommandService().execute(body.extract[MessageModel])
        case CommandNames.answer                   => AnswerCommandService().execute(body.extract[AnswerModel])
        case CommandNames.createQuestion           => CreateQuestionCommandService().execute(body.extract[CreateQuestionModel])
        case CommandNames.ackEvents                => AckEventsCommandService().execute(body.extract[AckEventsModel])
        case CommandNames.heartbeat                => Future.successful(None)
      }
      addAuthContext(cmd, response)
    }

    def addAuthContext(cmd: CommandName, response: Future[Option[_]]): FutureResponse = {
      val newAuthCtx: Future[Option[AuthContext]] = response.map {
        case Some(CommandModel(CommandNames.loginResponse, model: LoginResponseModel)) =>
          Option(new StandardUserAuthContext(new User(model.user.id), model.groups.map(_.id).toSet))
        case _ => None
      }
      combineResponseAndAuthCtx(response, newAuthCtx)
    }

    def combineResponseAndAuthCtx(response: Future[Option[_]],
                                  newAuthCtx: Future[Option[AuthContext]]): FutureResponse = {
      newAuthCtx.flatMap { newAuthCtx =>
        response.map {
          case Some(response) => returnResult(response, newAuthCtx)
          case None           => Right(CommandProcessResult(None, newAuthCtx))
        }
      }
    }

    def serializeResult(msg: Any) = Tryo(Serialization.write(msg.asInstanceOf[Object]))

    def returnResult(msg: Any, newAuthCtx: Option[AuthContext]): Response = {
      serializeResult(msg) match {
        case Some(json: String) => Right(CommandProcessResult(Option(json), newAuthCtx))
        case None               => returnError(s"failed to serialize message: $msg")
      }
    }

    def returnError(reason: String, args: Any*): Response = {
      returnError(ApiErrorService().statusCodeSeed(Errors.externalErrorText,
        StatusCodes.InternalServerError, reason.format(args: _*)))
    }

    def returnError(code: ErrorCode, reason: String, args: Any*): Response = {
      returnError(ApiErrorService().errorCodeSeed(
        Errors.externalErrorText, StatusCodes.BadRequest, code, reason.format(args: _*)))
    }

    def returnError(apiError: ApiError): Response = {
      Left(CommandProcessError(apiError))
    }

  }

}

object CommandProcessingServiceImpl {

  object Errors {

    val externalErrorText = "Failed to process command."

  }

}

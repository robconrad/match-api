/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.socket.command.impl

import base.common.lib.Tryo
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.{ AuthContext, ChannelContext, StandardUserAuthContext }
import base.entity.command.CommandNames
import base.entity.command.CommandNames.CommandName
import base.entity.command.model.CommandModel
import base.entity.error.ApiErrorService
import base.entity.error.model.ApiError
import base.entity.group.model.{ AcceptInviteModel, DeclineInviteModel, InviteModel }
import base.entity.group.{ AcceptInviteCommandService, DeclineInviteCommandService, InviteCommandService }
import base.entity.json.JsonFormats
import base.entity.logging.AuthLoggable
import base.entity.message.MessageCommandService
import base.entity.message.model.MessageModel
import base.entity.question.model.{ AnswerModel, QuestionsModel }
import base.entity.question.{ AnswerCommandService, QuestionsCommandService }
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

    def parseInput(input: String): FutureResponse = {
      try {
        extractCommand(JsonMethods.parse(input)) recover {
          case t => returnError("parse threw exception %s", t)
        }
      } catch {
        case e: Exception => returnError("parse threw exception %s", e)
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

    def processCommand(cmd: CommandName, body: JObject): FutureResponse = {
      val response: Future[Option[_]] = cmd match {
        case CommandNames.registerPhone   => RegisterPhoneCommandService().execute(body.extract[RegisterPhoneModel])
        case CommandNames.verifyPhone     => VerifyPhoneCommandService().execute(body.extract[VerifyPhoneModel])
        case CommandNames.login           => LoginCommandService().execute(body.extract[LoginModel])
        case CommandNames.invite          => InviteCommandService().execute(body.extract[InviteModel])
        case CommandNames.acceptInvite    => AcceptInviteCommandService().execute(body.extract[AcceptInviteModel])
        case CommandNames.declineInvite   => DeclineInviteCommandService().execute(body.extract[DeclineInviteModel])
        case CommandNames.questions       => QuestionsCommandService().execute(body.extract[QuestionsModel])
        case CommandNames.message         => MessageCommandService().execute(body.extract[MessageModel])
        case CommandNames.answer          => AnswerCommandService().execute(body.extract[AnswerModel])
        case CommandNames.heartbeat       => Future.successful(None)
      }
      addAuthContext(cmd, response)
    }

    def addAuthContext(cmd: CommandName, response: Future[Option[_]]): FutureResponse = {
      val newAuthCtx: Future[Option[AuthContext]] = response.map {
        case Some(CommandModel(CommandNames.loginResponse, model: LoginResponseModel)) =>
          Option(new StandardUserAuthContext(new User(model.userId)))
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

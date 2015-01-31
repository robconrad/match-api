/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:44 AM
 */

package base.socket.command.impl

import base.common.lib.Tryo
import base.common.service.ServiceImpl
import base.entity.auth.context.{ ChannelContext, AuthContext, StandardUserAuthContext }
import base.entity.command.CommandNames
import base.entity.command.CommandNames.CommandName
import base.entity.command.model.CommandModel
import base.entity.group.InviteCommandService
import base.entity.group.model.InviteModel
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
import org.json4s.JValue
import org.json4s.JsonAST.{ JObject, JString }
import org.json4s.native.{ JsonMethods, Serialization }

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of CommandServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CommandProcessingServiceImpl extends ServiceImpl with CommandProcessingService with AuthLoggable {

  implicit val formats = JsonFormats.withModels

  implicit def response2Future(r: Response) = Future.successful(r)

  def process(input: String)(implicit channelCtx: ChannelContext) = {
    new ProcessCommand().parseInput(input)
  }

  private[impl] class ProcessCommand()(implicit channelCtx: ChannelContext) {

    def parseInput(input: String): FutureResponse = {
      try {
        val f = extractCommand(JsonMethods.parse(input))
        f.onFailure {
          case t => error("parse threw exception %s", t)
        }
        f
      } catch {
        case e: Exception => error("parse threw exception %s", e)
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
                    case _             => error("unable to parse body from message: %s", json)
                  }
                case None => error("unable to match cmd from message: %s", json)
              }
            case _ => error("unable to parse cmd from message: %s", json)
          }
        case _ => error("no json received in message")
      }
    }

    def processCommand(cmd: CommandName, body: JObject): FutureResponse = {
      val response: Future[Option[_]] = cmd match {
        case CommandNames.`registerPhone` => RegisterPhoneCommandService().execute(body.extract[RegisterPhoneModel])
        case CommandNames.`verifyPhone`   => VerifyPhoneCommandService().execute(body.extract[VerifyPhoneModel])
        case CommandNames.login           => LoginCommandService().execute(body.extract[LoginModel])
        case CommandNames.invite          => InviteCommandService().execute(body.extract[InviteModel])
        case CommandNames.questions       => QuestionsCommandService().execute(body.extract[QuestionsModel])
        case CommandNames.message         => MessageCommandService().execute(body.extract[MessageModel])
        case CommandNames.answer          => AnswerCommandService().execute(body.extract[AnswerModel])
        case CommandNames.heartbeat       => Future.successful(None)
        case _ =>
          warn("command %s not handled", cmd)
          Future.successful(None)
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
          case Some(response) => result(response, newAuthCtx)
          case None           => Right(CommandProcessResult(None, newAuthCtx))
        }
      }
    }

    def result(msg: Any, newAuthCtx: Option[AuthContext]): Response = {
      Tryo(Serialization.write(msg.asInstanceOf[Object])) match {
        case Some(json: String) => Right(CommandProcessResult(Option(json), newAuthCtx))
        case _                  => error(s"failed to encode message: $msg")
      }
    }

    def error(reason: String, args: Any*): Response = {
      Left(CommandProcessError(reason.format(args: _*)))
    }

  }

}

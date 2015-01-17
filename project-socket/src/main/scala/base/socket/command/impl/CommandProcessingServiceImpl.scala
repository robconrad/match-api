/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 12:18 PM
 */

package base.socket.command.impl

import base.common.lib.Tryo
import base.common.service.ServiceImpl
import base.entity.auth.context.{ AuthContext, StandardUserAuthContext }
import base.entity.group.InviteCommandService
import base.entity.group.model.InviteModel
import base.entity.json.JsonFormats
import base.entity.logging.AuthLoggable
import base.entity.message.MessageCommandService
import base.entity.message.model.MessageModel
import base.entity.question.model.{ AnswerModel, QuestionsModel }
import base.entity.question.{ AnswerCommandService, QuestionsCommandService }
import base.entity.user.model.{ LoginModel, LoginResponseModel, RegisterModel, VerifyModel }
import base.entity.user.{ LoginCommandService, RegisterCommandService, User, VerifyCommandService }
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

  implicit val formats = JsonFormats.withEnumsAndFields

  implicit def response2Future(r: Response) = Future.successful(r)

  def process(input: String)(implicit authCtx: AuthContext) = {
    new ProcessCommand().parseInput(input)
  }

  private[impl] class ProcessCommand()(implicit authCtx: AuthContext) {

    def parseInput(input: String): FutureResponse = {
      try {
        val f = extractCommand(JsonMethods.parse(input))
        f.onFailure {
          case t => error("parse threw exception %s", t)
        }
        f
      } catch {
        case e: Exception =>
          error("parse threw exception %s", e)
      }
    }

    // expecting { "cmd": "$name", "body": { $body } }
    def extractCommand(json: JValue): FutureResponse = {
      json match {
        case json: JObject =>
          json \ "cmd" match {
            case cmd: JString =>
              json \ "body" match {
                case body: JObject => processCommand(cmd.s, body)
                case _             => error("unable to parse body from message: %s", json)
              }
            case _ => error("unable to parse cmd from message: %s", json)
          }
        case _ => error("no json received in message")
      }
    }

    def processCommand(cmd: String, body: JObject): FutureResponse = {
      val response: Option[Future[_]] = cmd match {
        case RegisterCommandService.`cmd`  => Option(RegisterCommandService().execute(body.extract[RegisterModel]))
        case VerifyCommandService.`cmd`    => Option(VerifyCommandService().execute(body.extract[VerifyModel]))
        case LoginCommandService.`cmd`     => Option(LoginCommandService().execute(body.extract[LoginModel]))
        case InviteCommandService.`cmd`    => Option(InviteCommandService().execute(body.extract[InviteModel]))
        case QuestionsCommandService.`cmd` => Option(QuestionsCommandService().execute(body.extract[QuestionsModel]))
        case MessageCommandService.`cmd`   => Option(MessageCommandService().execute(body.extract[MessageModel]))
        case AnswerCommandService.`cmd`    => Option(AnswerCommandService().execute(body.extract[AnswerModel]))
        case "heartbeat"                   => None
        case cmd                           => error("'%s' is not a valid command for this handler", cmd); None
      }
      addAuthContext(cmd, response)
    }

    def addAuthContext(cmd: String, response: Option[Future[_]]): FutureResponse = {
      val newAuthCtx: Future[Option[AuthContext]] = response match {
        case Some(future) if cmd == "login" => future.map {
          case Left(error)                      => None
          case Right(model: LoginResponseModel) => Option(new StandardUserAuthContext(new User(model.userId)))
        }
        case _ => Future.successful(None)
      }
      combineResponseAndAuthCtx(response, newAuthCtx)
    }

    def combineResponseAndAuthCtx(response: Option[Future[_]],
                                  newAuthCtx: Future[Option[AuthContext]]): FutureResponse = {
      newAuthCtx.flatMap { newAuthCtx =>
        response match {
          case Some(response) => response.map(response => parseResponse(response, newAuthCtx))
          case None           => Right(CommandProcessResult(None, newAuthCtx))
        }
      }
    }

    def parseResponse(response: Any, newAuthCtx: Option[AuthContext]): Response = {
      response match {
        case Left(msg: Object)  => result(msg, newAuthCtx)
        case Right(msg: Object) => result(msg, newAuthCtx)
        case msg                => error("msg isn't left or right: %s", msg)
      }
    }

    def result(msg: Object, newAuthCtx: Option[AuthContext]): Response = {
      Tryo(Serialization.write(msg)) match {
        case Some(json: String) => Right(CommandProcessResult(Option(json), newAuthCtx))
        case _                  => error(s"failed to encode message: $msg")
      }
    }

    def error(reason: String, args: Any*): Response = {
      Left(CommandProcessError(reason.format(args: _*)))
    }

  }

}

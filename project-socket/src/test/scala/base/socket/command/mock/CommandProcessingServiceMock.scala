/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:58 PM
 */

package base.socket.command.mock

import base.entity.auth.context.ChannelContext
import base.socket.command.CommandProcessingService
import base.socket.command.CommandProcessingService.{ CommandProcessResult, FutureResponse }

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of CommandProcessingServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CommandProcessingServiceMock(
  processResult: FutureResponse = Future.successful(Right(CommandProcessResult(None, None))))
    extends CommandProcessingService {

  def process(input: String)(implicit channelCtx: ChannelContext) = processResult

}

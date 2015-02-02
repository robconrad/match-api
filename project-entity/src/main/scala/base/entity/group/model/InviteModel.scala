/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 5:48 PM
 */

package base.entity.group.model

import base.entity.api.ApiStrings.User._
import base.entity.group.model.impl.InviteModelImpl
import base.entity.model.{Model, ModelCompanion}
import com.wordnik.swagger.annotations.{ApiModel, ApiModelProperty}

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
trait InviteModel extends Model {

  @(ApiModelProperty @field)(required = true, value = passwordDesc) def phone: String
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def pictureUrl: Option[String]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def name: Option[String]

}
// format: ON

object InviteModel extends ModelCompanion[InviteModel, InviteModelImpl]

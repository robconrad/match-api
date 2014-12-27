/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:13 PM
 */

package base.entity.apiKey.model

import base.common.time.DateTimeHelper
import base.entity.ApiStrings.Keys._
import base.entity.model.ModelImplicits
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field

/**
 * API describable API Key
 * @author rconrad
 */
// format: OFF
@ApiModel(value = keyDesc)
case class ApiKey(
  @(ApiModelProperty @field)(required = true,  value = idDesc)          id: String,
  @(ApiModelProperty @field)(required = true,  value = keyDesc)         key: String,
  @(ApiModelProperty @field)(required = true,  value = activeDesc)      active: Boolean,
  @(ApiModelProperty @field)(required = true,  value = createdAtDesc)   createdAt: String
)
// format: ON

object ApiKey extends ModelImplicits with DateTimeHelper {

}

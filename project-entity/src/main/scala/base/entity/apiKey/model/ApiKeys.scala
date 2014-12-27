/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:13 PM
 */

package base.entity.apiKey.model

import base.entity.ApiStrings.Keys._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field

/**
 * API describable ApiKeysResponse
 * @author rconrad
 */
// format: OFF
@ApiModel(description = keysResponse)
case class ApiKeys(
  @(ApiModelProperty @field)(required = true, value = keysDesc) data: List[ApiKey]
)
// format: ON

object ApiKeys {

}

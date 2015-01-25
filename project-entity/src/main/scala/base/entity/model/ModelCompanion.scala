/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:36 AM
 */

package base.entity.model

import base.entity.json.JsonFormats
import org.json4s.{ CustomSerializer, Extraction }

/**
 * {{ Describe the high level purpose of Model here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class ModelCompanion[M <: Model, Mi <: M](implicit val m: Manifest[M], mi: Manifest[Mi]) {

  implicit val formats = JsonFormats.withEnumsAndFields

  def serializer =
    new CustomSerializer[M](format => ({
      case x => x.extract[Mi].asInstanceOf[M]
    }, {
      case x: M => Extraction.decompose(x)
    }))

}

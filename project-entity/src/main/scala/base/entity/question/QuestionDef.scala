/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 10:20 AM
 */

package base.entity.question

import java.util.UUID

/**
 * {{ Describe the high level purpose of QuestionDef here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class QuestionDef(id: UUID, a: String, b: Option[String] = None)

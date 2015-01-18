/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 10:04 AM
 */

package base.entity.question

import java.util.UUID

import base.entity.question.QuestionSides.QuestionSide

/**
 * {{ Describe the high level purpose of QuestionIdComposite here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class QuestionIdComposite private[question] (question: QuestionDef, side: QuestionSide, inverse: Boolean = false) {
  override lazy val toString = {
    val sideOut = question.b.isEmpty match {
      case true => QuestionSides.SIDE_A
      case false => inverse match {
        case false => side
        case true => side match {
          case QuestionSides.SIDE_A => QuestionSides.SIDE_B
          case QuestionSides.SIDE_B => QuestionSides.SIDE_A
        }
      }
    }
    question.id.toString + sideOut
  }
}

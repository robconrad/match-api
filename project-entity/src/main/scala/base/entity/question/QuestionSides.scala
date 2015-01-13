/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 7:48 PM
 */

package base.entity.question

/**
 * {{ Describe the high level purpose of QuestionSides here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object QuestionSides extends Enumeration {
  type QuestionSide = Value

  val SIDE_A = Value("a")
  val SIDE_B = Value("b")

}

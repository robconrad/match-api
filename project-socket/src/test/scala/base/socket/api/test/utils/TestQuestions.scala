/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 1:17 PM
 */

package base.socket.api.test.utils

import java.util.UUID

import base.entity.question.{QuestionSides, QuestionDef}
import base.entity.question.model.QuestionModel

/**
 * {{ Describe the high level purpose of QuestionDefs here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TestQuestions {

  val defs = List(
    ("65b76c8e-a9b3-4eda-b6dc-ebee6ef78a04", "Doin' it with the lights off", None),
    ("2c75851f-1a87-40ab-9f66-14766fa12c6c", "Liberal use of chocolate sauce", None),
    ("a8d08d2d-d914-4e7b-8ff4-6a9dde02002c", "Giving buttsex", Option("Receiving buttsex")),
    ("543c57e3-54ba-4d9b-8ed3-c8f2e394b18d", "Dominating", Option("Being dominated"))
  ).map(q => QuestionDef(UUID.fromString(q._1), q._2, q._3))

  val models = ListUtils.sortQuestions(defs.map(QuestionModel(_, QuestionSides.SIDE_A)) ++
    defs.collect {
      case q if q.b.isDefined => QuestionModel(q, QuestionSides.SIDE_B)
    })

  def apply(index: Int) = defs(index)

  def filteredModels(index: Int) = models.filter(_.id != defs(index).id)
  def filteredModels(indices: List[Int]) = models.filter { model =>
    !indices.map(defs(_).id).contains(model.id)
  }

}

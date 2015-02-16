/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:02 PM
 */

package base.socket.api.test.util

import java.util.UUID

import base.entity.question.QuestionDef
import base.entity.question.QuestionSides._
import base.entity.question.model.QuestionModel

/**
 * {{ Describe the high level purpose of QuestionDefs here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TestQuestions {

  val standardDefs = List(
    ("65b76c8e-a9b3-4eda-b6dc-ebee6ef78a04", "Doin' it with the lights off", None),
    ("2c75851f-1a87-40ab-9f66-14766fa12c6c", "Liberal use of chocolate sauce", None),
    ("a8d08d2d-d914-4e7b-8ff4-6a9dde02002c", "Giving buttsex", Option("Receiving buttsex")),
    ("543c57e3-54ba-4d9b-8ed3-c8f2e394b18d", "Dominating", Option("Being dominated"))
  ).map(q => QuestionDef(UUID.fromString(q._1), q._2, q._3))

  private var groupDefs = Map[UUID, Set[QuestionDef]]()

  def defs(groupId: UUID) = standardDefs ++ groupDefs.getOrElse(groupId, Set())

  private implicit def defs2Models(defs: List[QuestionDef]): List[QuestionModel] =
    ListUtils.sortQuestions(defs.map(QuestionModel(_, SIDE_A)) ++
      defs.collect {
        case q if q.b.isDefined => QuestionModel(q, SIDE_B)
      })

  def standardModels: List[QuestionModel] = standardDefs

  def models(groupId: UUID): List[QuestionModel] = defs(groupId)

  def apply(groupId: UUID, index: Int) = defs(groupId)(index)

  def filteredModels(groupId: UUID, index: Int, side: QuestionSide = SIDE_A): List[QuestionModel] =
    filteredModels(groupId, List((index, side)))

  def filteredModels(groupId: UUID, indices: List[(Int, QuestionSide)]): List[QuestionModel] =
    models(groupId).filter { model =>
      !indices.map {
        case (index, side) =>
          (defs(groupId)(index).id, side)
      }.contains((model.id, model.side))
    }

  def addGroupDef(groupId: UUID, `def`: QuestionDef): Unit = {
    groupDefs += groupId -> (groupDefs.getOrElse(groupId, Set()) ++ Set(`def`))
  }

}

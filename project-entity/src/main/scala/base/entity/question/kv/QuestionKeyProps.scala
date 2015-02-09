/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 7:41 PM
 */

package base.entity.question.kv

import base.entity.kv.KeyProp

/**
 * {{ Describe the high level purpose of UserKeyProps here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[question] object QuestionKeyProps {

  object CreatorIdProp extends KeyProp("creatorId")
  object SideAProp extends KeyProp("a")
  object SideBProp extends KeyProp("b")

}

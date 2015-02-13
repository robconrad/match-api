/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:46 PM
 */

package base.entity.kv.serializer

import base.entity.kv.serializer.impl._

/**
 * {{ Describe the high level purpose of SerializerImplicits here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object SerializerImplicits {

  implicit val eventModelSerializer = EventModelSerializer
  implicit val dateTimeSerializer = DateTimeSerializer
  implicit val facebookInfoSerializer = FacebookInfoSerializer
  implicit val orderedIdPairSerializer = OrderedIdPairSerializer
  implicit val sortedIdPairSerializer = SortedIdPairSerializer
  implicit val questionIdCompositeSerializer = QuestionIdCompositeSerializer
  implicit val userPhoneSerializer = UserPhoneSerializer

}

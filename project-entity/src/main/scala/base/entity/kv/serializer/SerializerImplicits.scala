/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.kv.serializer

import base.entity.kv.serializer.impl._
import scredis.serialization.{ BytesReader, BytesWriter, UUIDReader, UUIDWriter }

/**
 * {{ Describe the high level purpose of SerializerImplicits here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object SerializerImplicits {

  implicit val booleanSerializer = BooleanSerializer
  implicit val dateTimeSerializer = DateTimeSerializer
  implicit val eventModelSerializer = EventModelSerializer
  implicit val facebookInfoSerializer = FacebookInfoSerializer
  implicit val intPhoneSerializer = IntSerializer
  implicit val longPhoneSerializer = LongSerializer
  implicit val orderedIdPairSerializer = OrderedIdPairSerializer
  implicit val questionIdCompositeSerializer = QuestionIdCompositeSerializer
  implicit val shortPhoneSerializer = ShortSerializer
  implicit val sortedIdPairSerializer = SortedIdPairSerializer
  implicit val userPhoneSerializer = UserPhoneSerializer

  implicit val byteaReader = BytesReader
  implicit val byteaWriter = BytesWriter

  implicit val uuidReader = UUIDReader
  implicit val uuidWriter = UUIDWriter

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:54 PM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of MakeKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait MakeKey {

  def make[T](id: Any)(implicit m: Manifest[T]): T = KeyFactoryService().make[T](id)

}

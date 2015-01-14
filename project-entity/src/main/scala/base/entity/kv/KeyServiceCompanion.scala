/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 6:32 PM
 */

package base.entity.kv

import base.common.service.Services

/**
 * A KV is a key-value pair in the KV store
 * @author rconrad
 */
abstract class KeyServiceCompanion[T <: KeyService[_]](implicit m: Manifest[T]) {

  def apply() = Services.apply[T]

}

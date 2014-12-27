/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.lib

import java.nio.charset.Charset

/**
 * Central location for encoding information
 * @author rconrad
 */
object Encoding {

  val UTF8 = "UTF-8"
  val CHARSET_UTF8 = Charset.forName(Encoding.UTF8)

}

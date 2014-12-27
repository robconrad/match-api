/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:13 PM
 */

package base.entity

import base.common.lib.{ Currencies, Languages }
import base.entity.Tables.profile.simple._

/**
 * Provides slick type converters for enums used in database tables
 * @author rconrad
 */
object SlickConverters {

  type Currency = Currencies.Currency
  type Language = Languages.Language

  implicit val currencyMapper =
    MappedColumnType.base[Currency, String](_.toString, Currencies.withName)

  implicit val languageMapper =
    MappedColumnType.base[Language, String](_.toString, Languages.withName)

}

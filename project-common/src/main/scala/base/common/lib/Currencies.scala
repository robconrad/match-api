/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.lib

/**
 * Description of "ISO" (not really) currencies that we accept at API level
 * @author rconrad
 */
object Currencies extends Enumeration {
  type Currency = Value
  implicit def asString(c: Currency) = c.toString
  implicit def asValue(s: String) = withName(s)

  /**
   * Render an amount in the provided currency to string with the currency's associated precision
   */
  def preciselyString(c: Currency, a: Double): String = s"%.${precision(c)}f".format(a)
  def preciselyDouble(c: Currency, a: Double): Double = preciselyString(c, a).toDouble

  /**
   * The amount within which two doubles can differ and still be considered the same for a given currency
   */
  def allowedDelta(c: Currency) = 1d / Math.pow(10d, precision(c))

  val USD = Value("USD")
  val KRW = Value("KRW")
  val BTC = Value("BTC")

  val fromCurrencies = Set(USD, KRW)
  val toCurrencies = Set(BTC)
  val paidCurrencies = fromCurrencies

  /**
   * Defines the expected and allowed precision for each currency. Extremely important for
   *  security hash calculation because decimal numbers are converted to string then hashed
   *  via md5 - so any precision differences would manifest as non-matching hashes.
   */
  val precision = Map(
    USD -> 2,
    KRW -> 0,
    BTC -> 9 // 100,000,000 satoshi per BTC
  )

  // ensure precision map includes all currencies
  assert(precision.keys.size == values.size &&
    !values.map(precision.keys.toList.contains).exists(p => !p))

}

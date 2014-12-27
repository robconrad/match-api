/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.model

import java.util.UUID

import base.entity.auth.context.AuthContext

/**
 * Swagger is a piece of shit so we convert everything to dumbest possible type in order to keep it's shitty UI usable
 * @author rconrad
 */
trait ModelImplicits {

  implicit def bigDecimal2Double(d: BigDecimal) = d.toDouble

  implicit def optionBigDecimal2Double(d: Option[BigDecimal]) = d.map(_.toDouble).getOrElse(0d)

  implicit def optionString2String(s: Option[String]) = s.getOrElse("")

  implicit def uuid2String(u: UUID) = u.toString

  implicit def optionField2String(f: Option[Field[_]]) = f.map(_.toString)

  implicit def field2String(f: Field[_]) = f.toString

  def hideIfPublic[T](o: T)(implicit authCtx: AuthContext): Option[T] = hideIfPublic(Option(o))

  def hideIfPublic[T](o: Option[T])(implicit authCtx: AuthContext): Option[T] = authCtx.authType.hideIfPublic(o)

}

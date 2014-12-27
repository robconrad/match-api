/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:42 PM
 */

package scala.slick.model.codegen

import scala.slick.driver.H2Driver


/**
 * Application whose sole purpose is to generate slick table access code
 * @author rconrad
 */
object DatabaseCodeGenerator {

  val coverageOff = "$COVERAGE-OFF$"
  val styleCheckOff = "scalastyle:off"
  val generatedClassName = "Tables"
  val generatedClassPackage = "base.entity"
  val url = s"jdbc:h2:mem:test;DATABASE_TO_UPPER=false;INIT=RUNSCRIPT FROM 'project-entity/src/main/resources/evolutions/gen-slick.sql'"
  val h2Driver = "org.h2.Driver"
  val jdbcDriver = "base.entity.db.DbConfigService().dbDriver"
  val jdbcProfile = "scala.slick.driver.JdbcProfile"

  val entities = Map[String, String]()

  val methods = Map[String,String](
  )

  val rowExtends = Map[String, String](
  )

  val types = Map(
    "java.sql.Timestamp" -> "DateTime"
  )

  val nameTypes = Map(
    "api_keys.type" -> "ApiKeyType"
  )

  val globalNameTypes = Map(
    "uuid" -> "UUID"
  )

  /**
   * Project entry point. Dead simple.
   */
  def main(args: Array[String]) = {
    val db = H2Driver.simple.Database.forURL(url, driver = h2Driver)
    val model = db.withSession(H2Driver.createModel(_))

    /**
     * Customize the standard generator to include the entities, methods, etc. defined above
     */
    new scala.slick.model.codegen.SourceCodeGenerator(model) {
      def writeToFile() {
        this.writeStringToFile(getPackageCode, args(0), generatedClassPackage, s"$generatedClassName.scala")
      }

      // naive de-pluralizer -- add special cases for tables that need more than the simple removal of the last letter
      def depluralize(s: String) = s.split("_").map {
        case s if s.substring(s.length - 3) == "ses" => s.substring(0, s.length - 2)
        case s if s.substring(s.length - 1) == "s" => s.substring(0, s.length - 1)
        case s => s
      }.mkString("_")

      override def entityName = name => super.entityName(depluralize(name))

      override def Table = new Table(_) { table =>

        private def myEntityName = entityName(model.name.table)

        private def getRowFactory = {
          val cols = columns.filterNot(c => c.rawName == "id" || c.rawName == "uuid")
          val input = cols.map { c =>
            val nameType = s"${c.name}: ${c.exposedType}"
            c.default.map(v => s"$nameType = $v").getOrElse {
              c.rawName match {
                case "createdAt" | "updatedAt" => s"$nameType = now"
                case _ => nameType
              }
            }
          }.mkString(", ")
          val output = cols.map(_.name).mkString(", ")
          val id = columns.exists(_.rawName == "id") match {
            case true => "0L, "
            case false => ""
          }
          val uuid = columns.exists(_.rawName == "uuid") match {
            case true => "RandomService().uuid, "
            case false => ""
          }
          s"def ${myEntityName}Builder($input) = {\n  $myEntityName($id$uuid$output)\n}"
        }

        override def EntityType = new EntityType {
          private def getRowClassBody = {
            val mergeFunctions = columns.filterNot(c => c.rawName == "id" || c.rawName == "uuid").map { col =>
              val name = col.name.toString
              val ucFirstName = Character.toUpperCase(col.rawName.charAt(0)) + col.rawName.substring(1)
              val (typ, fun) = col.model.nullable match {
                case true => (col.actualType, "mergeOpt")
                case false => (s"Option[${col.actualType}]", "merge")
              }
              s"def merge$ucFirstName($name: $typ) = copy($name = $fun($name, this.$name))"
            } ++ methods.filter(_._1 == name).map(_._2)
            s" {\n  ${mergeFunctions.mkString("\n  ")}\n}"
          }

          private def getExtends = rowExtends.get(name.toString).map(cls => s" extends $cls").getOrElse("")

          override def code = super.code + getExtends + getRowClassBody + (entities.get(name.toString) match {
            case Some(entity) => s"\n$entity"
            case _            => ""
          })
        }

        override def TableClass = new TableClass {
          override def body = (methods.get(name.toString) match {
            case Some(method) => Seq(Seq(method))
            case _            => Seq()
          }) ++ super.body
        }

        override def Column = new Column(_) {
          override def rawType = (nameTypes.get(model.table.table + "." + model.name), globalNameTypes.get(model.name), types.get(model.tpe)) match {
            case (Some(typ), _, _) => typ
            case (_, Some(typ), _) => typ
            case (_, _, Some(typ)) => typ
            case _              => super.rawType
          }
        }

        override def code = super.code ++ Seq(getRowFactory)

      }

      /**
       * Class preamble and package info as string, combined with generated code
       */
      def getPackageCode = s"""
/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad on 7/2014
 */

package $generatedClassPackage
// AUTO-GENERATED Slick data model
// $coverageOff
// $styleCheckOff
/**
 * Stand-alone Slick data model
 * @author auto-generated
 */
private[entity] object $generatedClassName extends {
  val profile = $jdbcDriver
} with $generatedClassName

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
private[entity] trait $generatedClassName extends base.common.time.DateTimeHelper {
  val profile: $jdbcProfile
  import base.common.random.RandomService
  import base.entity.model._
  import base.entity.SlickConverters._
  import com.github.tototoshi.slick.JdbcJodaSupport._
  import java.util.UUID
  import org.joda.time.DateTime
  import profile.simple._

  // merges optional input data with existing data where input data always wins
  private def merge[T](input: Option[T], fallback: T) = input match {
    case Some(input) => input
    case None => fallback
  }

  // merges optional input data with existing optional data where input data always wins
  private def mergeOpt[T](input: Option[T], fallback: Option[T]) = input match {
    case Some(i) => input
    case None => fallback
  }

  ${this.indent(this.code)}
}
      """.trim()

    }.writeToFile()
  }
  
}

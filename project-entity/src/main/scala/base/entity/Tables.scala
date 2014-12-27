/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:56 PM
 */

package base.entity
// AUTO-GENERATED Slick data model
// $COVERAGE-OFF$
// scalastyle:off
/**
 * Stand-alone Slick data model
 * @author auto-generated
 */
private[entity] object Tables extends {
  val profile = base.entity.db.DbConfigService().dbDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
private[entity] trait Tables extends base.common.time.DateTimeHelper {
  val profile: scala.slick.driver.JdbcProfile
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
    case None        => fallback
  }

  // merges optional input data with existing optional data where input data always wins
  private def mergeOpt[T](input: Option[T], fallback: Option[T]) = input match {
    case Some(i) => input
    case None    => fallback
  }

  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{ GetResult => GR }

  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = ApiKeys.ddl ++ Evolutions.ddl ++ Users.ddl

  /**
   * Entity class storing rows of table ApiKeys
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param uuid Database column uuid
   *  @param value Database column value
   *  @param createdAt Database column created_at
   *  @param active Database column active Default(true)
   */
  case class ApiKeyRow(id: Long, uuid: UUID, value: String, createdAt: DateTime, active: Boolean = true) {
    def mergeValue(value: Option[String]) = copy(value = merge(value, this.value))
    def mergeCreatedAt(createdAt: Option[DateTime]) = copy(createdAt = merge(createdAt, this.createdAt))
    def mergeActive(active: Option[Boolean]) = copy(active = merge(active, this.active))
  }
  /** GetResult implicit for fetching ApiKeyRow objects using plain SQL queries */
  implicit def GetResultApiKeyRow(implicit e0: GR[Long], e1: GR[UUID], e2: GR[String], e3: GR[DateTime], e4: GR[Boolean]): GR[ApiKeyRow] = GR {
    prs =>
      import prs._
      ApiKeyRow.tupled((<<[Long], <<[UUID], <<[String], <<[DateTime], <<[Boolean]))
  }
  /** Table description of table api_keys. Objects of this class serve as prototypes for rows in queries. */
  class ApiKeys(_tableTag: Tag) extends Table[ApiKeyRow](_tableTag, "api_keys") {
    def * = (id, uuid, value, createdAt, active) <> (ApiKeyRow.tupled, ApiKeyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, uuid.?, value.?, createdAt.?, active.?).shaped.<>({ r => import r._; _1.map(_ => ApiKeyRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column uuid  */
    val uuid: Column[UUID] = column[UUID]("uuid")
    /** Database column value  */
    val value: Column[String] = column[String]("value")
    /** Database column created_at  */
    val createdAt: Column[DateTime] = column[DateTime]("created_at")
    /** Database column active Default(true) */
    val active: Column[Boolean] = column[Boolean]("active", O.Default(true))

    /** Uniqueness Index over (uuid) (database name u_api_keys_uuid_INDEX_3) */
    val index1 = index("u_api_keys_uuid_INDEX_3", uuid, unique = true)
    /** Uniqueness Index over (value) (database name u_api_keys_value_INDEX_3) */
    val index2 = index("u_api_keys_value_INDEX_3", value, unique = true)
  }
  /** Collection-like TableQuery object for table ApiKeys */
  lazy val ApiKeys = new TableQuery(tag => new ApiKeys(tag))
  def ApiKeyRowBuilder(value: String, createdAt: DateTime = now, active: Boolean = true) = {
    ApiKeyRow(0L, RandomService().uuid, value, createdAt, active)
  }

  /**
   * Entity class storing rows of table Evolutions
   *  @param id Database column id
   *  @param createdAt Database column created_at
   */
  case class EvolutionRow(id: Long, createdAt: DateTime) {
    def mergeCreatedAt(createdAt: Option[DateTime]) = copy(createdAt = merge(createdAt, this.createdAt))
  }
  /** GetResult implicit for fetching EvolutionRow objects using plain SQL queries */
  implicit def GetResultEvolutionRow(implicit e0: GR[Long], e1: GR[DateTime]): GR[EvolutionRow] = GR {
    prs =>
      import prs._
      EvolutionRow.tupled((<<[Long], <<[DateTime]))
  }
  /** Table description of table evolutions. Objects of this class serve as prototypes for rows in queries. */
  class Evolutions(_tableTag: Tag) extends Table[EvolutionRow](_tableTag, "evolutions") {
    def * = (id, createdAt) <> (EvolutionRow.tupled, EvolutionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, createdAt.?).shaped.<>({ r => import r._; _1.map(_ => EvolutionRow.tupled((_1.get, _2.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id  */
    val id: Column[Long] = column[Long]("id")
    /** Database column created_at  */
    val createdAt: Column[DateTime] = column[DateTime]("created_at")

    /** Uniqueness Index over (id) (database name CONSTRAINT_INDEX_E) */
    val index1 = index("CONSTRAINT_INDEX_E", id, unique = true)
  }
  /** Collection-like TableQuery object for table Evolutions */
  lazy val Evolutions = new TableQuery(tag => new Evolutions(tag))
  def EvolutionRowBuilder(createdAt: DateTime = now) = {
    EvolutionRow(0L, createdAt)
  }

  /**
   * Entity class storing rows of table Users
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param uuid Database column uuid
   *  @param email Database column email
   *  @param passwordHash Database column password_hash
   *  @param passwordSalt Database column password_salt
   *  @param resetCode Database column reset_code Default(None)
   *  @param resetExpiresAt Database column reset_expires_at Default(None)
   *  @param createdAt Database column created_at
   *  @param active Database column active Default(true)
   */
  case class UserRow(id: Long, uuid: UUID, email: String, passwordHash: String, passwordSalt: String, resetCode: Option[String] = None, resetExpiresAt: Option[DateTime] = None, createdAt: DateTime, active: Boolean = true) {
    def mergeEmail(email: Option[String]) = copy(email = merge(email, this.email))
    def mergePasswordHash(passwordHash: Option[String]) = copy(passwordHash = merge(passwordHash, this.passwordHash))
    def mergePasswordSalt(passwordSalt: Option[String]) = copy(passwordSalt = merge(passwordSalt, this.passwordSalt))
    def mergeResetCode(resetCode: Option[String]) = copy(resetCode = mergeOpt(resetCode, this.resetCode))
    def mergeResetExpiresAt(resetExpiresAt: Option[DateTime]) = copy(resetExpiresAt = mergeOpt(resetExpiresAt, this.resetExpiresAt))
    def mergeCreatedAt(createdAt: Option[DateTime]) = copy(createdAt = merge(createdAt, this.createdAt))
    def mergeActive(active: Option[Boolean]) = copy(active = merge(active, this.active))
  }
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Long], e1: GR[UUID], e2: GR[String], e3: GR[Option[String]], e4: GR[Option[DateTime]], e5: GR[DateTime], e6: GR[Boolean]): GR[UserRow] = GR {
    prs =>
      import prs._
      UserRow.tupled((<<[Long], <<[UUID], <<[String], <<[String], <<[String], <<?[String], <<?[DateTime], <<[DateTime], <<[Boolean]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UserRow](_tableTag, "users") {
    def * = (id, uuid, email, passwordHash, passwordSalt, resetCode, resetExpiresAt, createdAt, active) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, uuid.?, email.?, passwordHash.?, passwordSalt.?, resetCode, resetExpiresAt, createdAt.?, active.?).shaped.<>({ r => import r._; _1.map(_ => UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7, _8.get, _9.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column uuid  */
    val uuid: Column[UUID] = column[UUID]("uuid")
    /** Database column email  */
    val email: Column[String] = column[String]("email")
    /** Database column password_hash  */
    val passwordHash: Column[String] = column[String]("password_hash")
    /** Database column password_salt  */
    val passwordSalt: Column[String] = column[String]("password_salt")
    /** Database column reset_code Default(None) */
    val resetCode: Column[Option[String]] = column[Option[String]]("reset_code", O.Default(None))
    /** Database column reset_expires_at Default(None) */
    val resetExpiresAt: Column[Option[DateTime]] = column[Option[DateTime]]("reset_expires_at", O.Default(None))
    /** Database column created_at  */
    val createdAt: Column[DateTime] = column[DateTime]("created_at")
    /** Database column active Default(true) */
    val active: Column[Boolean] = column[Boolean]("active", O.Default(true))

    /** Uniqueness Index over (uuid) (database name u_users_uuid_INDEX_6) */
    val index1 = index("u_users_uuid_INDEX_6", uuid, unique = true)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
  def UserRowBuilder(email: String, passwordHash: String, passwordSalt: String, resetCode: Option[String] = None, resetExpiresAt: Option[DateTime] = None, createdAt: DateTime = now, active: Boolean = true) = {
    UserRow(0L, RandomService().uuid, email, passwordHash, passwordSalt, resetCode, resetExpiresAt, createdAt, active)
  }
}
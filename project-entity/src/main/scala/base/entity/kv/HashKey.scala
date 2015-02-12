/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:30 PM
 */

package base.entity.kv

import base.entity.kv.Key.Prop

import scala.concurrent.Future

/**
 * This trait implements hash commands.
 *
 * @define e [[scredis.exceptions.RedisErrorResponseException]]
 * @define none `None`
 * @define true '''true'''
 * @define false '''false'''
 */
trait HashKey[K] extends ScredisKey[K] {
  
  /**
   * Deletes one or more hash fields.
   *
   * @note Specified fields that do not exist within this hash are ignored. If key does not exist,
   * it is treated as an empty hash and this command returns 0. Redis versions older than 2.4 can
   * only remove a field per call.
   *
   * @param fields field(s) to be deleted from hash
   * @return the number of fields that were removed from the hash, not including specified but non
   * existing fields
   *
   * @since 2.0.0
   */
  protected def del(fields: Prop*): Future[Long]
  
  /**
   * Determines if a hash field exists.
   *
   * @param field name of the field
   * @return $true if the hash contains field, $false if the hash does not contain it or
   * the key does not exists
   *
   * @since 2.0.0
   */
  protected def exists(field: Prop): Future[Boolean]
  
  /**
   * Returns the value of a hash field.
   *
   * @param field field name to retrieve
   * @return the value associated with field name, or $none when field is not present in the hash
   * or key does not exist
   *
   * @since 2.0.0
   */
  protected def get(field: Prop): Future[Option[Array[Byte]]]
  
  /**
   * Returns all the fields and values in a hash.
   *
   * @return key-value pairs stored in hash with key, or $none when hash is empty or key does not
   * exist
   *
   * @since 2.0.0
   */
  protected def getAll: Future[Option[Map[Prop, Array[Byte]]]]
  
  /**
   * Increments the integer value of a hash field by the given number.
   *
   * @note If key does not exist, a new key holding a hash is created. If field does not exist the
   * value is set to 0 before the operation is performed.
   *
   * @param field field name to increment
   * @param count increment
   * @return the value at field after the increment operation
   *
   * @since 2.0.0
   */
  protected def incrBy(field: Prop, count: Long): Future[Long]
  
  /**
   * Increments the float value of a hash field by the given amount.
   *
   * @note If key does not exist, a new key holding a hash is created. If field does not exist the
   * value is set to 0 before the operation is performed.
   *
   * @param field field name to increment
   * @param count increment
   * @return the value at field after the increment operation
   *
   * @since 2.6.0
   */
  protected def incrByFloat(field: Prop, count: Double): Future[Double]
  
  /**
   * Returns all the fields in a hash.
   *
   * @return set of field names or the empty set if the hash is empty or the key does not exist
   *
   * @since 2.0.0
   */
  protected def keys: Future[Set[Prop]]
  
  /**
   * Returns the number of fields contained in the hash stored at key.
   *
   * @return number of fields in the hash, or 0 if the key does not exist
   *
   * @since 2.0.0
   */
  protected def len: Future[Long]
  
  /**
   * Returns the values associated to the specified hash fields.
   *
   * @note For every field that does not exist, $none is returned.
   *
   * @param fields field(s) to retrieve
   * @return list of value(s) associated to the specified field name(s)
   *
   * @since 2.0.0
   */
  protected def mGet(fields: Prop*): Future[List[Option[Array[Byte]]]]
  
  /**
   * Returns a `Map` containing field-value pairs associated to the specified hash fields.
   *
   * @note Every non-existent field gets removed from the resulting `Map`.
   *
   * @param fields field(s) to retrieve
   * @return field-value pairs associated to the specified field name(s)
   *
   * @since 2.0.0
   */
  protected def mGetAsMap(fields: Prop*): Future[Map[String, Array[Byte]]]
  
  /**
   * Sets multiple hash fields to multiple values.
   *
   * @note This command overwrites any existing fields in the hash. If key does not exist, a new
   * key holding a hash is created
   *
   * @param fieldValuePairs field-value pair(s) to be set
   *
   * @since 2.0.0
   */
  protected def mSet(fieldValuePairs: Map[Prop, Array[Byte]]): Future[Unit]
  
  /**
   * Incrementally iterates through the fields of a hash.
   *
   * @param cursor the offset
   * @param matchOpt when defined, the command only returns elements matching the pattern
   * @param countOpt when defined, provides a hint of how many elements should be returned
   * @return a pair containing the next cursor as its first element and the list of fields
   * (key-value pairs) as its second element
   *
   * @since 2.8.0
   */
  protected def scan(
    cursor: Long,
    matchOpt: Option[String] = None,
    countOpt: Option[Int] = None
  ): Future[(Long, List[(Prop, Array[Byte])])]
  
  /**
   * Sets the string value of a hash field.
   *
   * @note If the field already exists in the hash, it is overwritten.
   *
   * @param field field name to set
   * @param value value to set
   * @return $true if field is a new field in the hash and value was set, $false if
   * field already exists and the value was updated
   *
   * @since 2.0.0
   */
  protected def set(field: Prop, value: Array[Byte]): Future[Boolean]
  
  /**
   * Sets the value of a hash field, only if the field does not exist.
   *
   * @param field field name to set
   * @param value value to set
   * @return $true if field is a new field in the hash and value was set, $false if
   * field already exists and no operation was performed
   *
   * @since 2.0.0
   */
  protected def setNX(field: Prop, value: Array[Byte]): Future[Boolean]

  /**
   * Returns all the values in a hash.
   *
   * @return list of values, or the empty list if hash is empty or key does not exist
   *
   * @since 2.0.0
   */
  protected def vals: Future[List[Array[Byte]]]
  
}

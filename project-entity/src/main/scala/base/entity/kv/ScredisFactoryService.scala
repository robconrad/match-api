/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:31 PM
 */

package base.entity.kv

import base.common.service.{ Service, ServiceCompanion }
import scredis.commands._

/**
 * {{ Describe the high level purpose of KvService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[entity] trait ScredisFactoryService extends Service {

  final val serviceManifest = manifest[ScredisFactoryService]

  def hashCommands: HashCommands with KeyCommands
  def keyCommands: KeyCommands
  def listCommands: ListCommands with KeyCommands
  def setCommands: SetCommands with KeyCommands
  def sortedSetCommands: SortedSetCommands with KeyCommands
  def stringCommands: StringCommands with KeyCommands

}

object ScredisFactoryService extends ServiceCompanion[ScredisFactoryService]

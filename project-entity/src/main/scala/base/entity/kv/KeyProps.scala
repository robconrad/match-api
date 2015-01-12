/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:52 PM
 */

package base.entity.kv

/**
 * Selection of globally used key props
 * @author rconrad
 */
object KeyProps {

  // timestamps
  object CreatedProp extends KeyProp("created")
  object UpdatedProp extends KeyProp("updated")

}

/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/27/15 6:34 PM
 */

package base.entity.facebook

/**
 * {{ Describe the high level purpose of FacebookInfo here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class FacebookInfo(id: String, firstName: String, gender: String, locale: String) {

  lazy val pictureUrl = s"http://graph.facebook.com/$id/picture?type=large"

}

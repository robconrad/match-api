/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.perm

import base.entity.error.ApiException
import spray.http.{ StatusCode, StatusCodes }

/**
 * Specific type of API Exception that indicates the AuthContext of the request does not have the permissions
 *  necessary to execute the desired action. Typically perms are checked at the API request layer, but some
 *  entity methods may double check perms and throw if necessary.
 * @author rconrad
 */
class PermException(msg: String,
                    t: Option[Throwable] = None,
                    status: StatusCode = StatusCodes.NotFound) extends ApiException(msg, t, status)

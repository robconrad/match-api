/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:32 PM
 */

package base.entity

/**
 * Container for strings used in API Documentation, specifically in the annotations used
 *  by Swagger. Unfortunately, annotations values must be final and must be primitive - meaning
 *  no string composition, no pulling from conf files, no ingenious language solutions.
 *  Still better than hand-maintaining docs.
 *
 * Coverage is turned off since the cov module can't determine whether final vals have been covered (weak, I know)
 *
 * NB: ALL STRINGS IN THIS FILE ARE CONSTANT - there is no interpolation or concatenation. This rule is required
 *     because many of these strings are used in annotations for swagger, and annotations must be constant native
 *     strings at the pre-compile stage.
 *
 * @author rconrad
 */
// $COVERAGE-OFF$
// scalastyle:off line.size.limit
// scalastyle:off file.size.limit
object ApiStrings {

  /**
   * Common
   */
  // headers
  final val headerPrefix = "X-Base-"

  // methods
  final val getMethod = "GET"
  final val postMethod = "POST"
  final val putMethod = "PUT"
  final val deleteMethod = "DELETE"

  // data types
  final val stringDataType = "string"
  final val boolDataType = "boolean"
  final val intDataType = "integer"
  final val decimalDataType = "decimal"
  final val longDataType = "long"
  final val uuidDataType = "java.lang.UUID"

  // param types
  final val formParamType = "form"
  final val headerParamType = "header"
  final val pathParamType = "path"
  final val queryParamType = "query"

  // response codes
  final val OKCode = 200
  final val OKCodeDesc = "Request completed successfully."

  final val foundCode = 302
  final val foundCodeDesc = "Redirect to a different URI."

  final val errorCode = 400
  final val errorCodeDesc = "An error occurred, input may not be valid."

  final val authErrorCode = 401
  final val authErrorCodeDesc = "Authorization headers were not accepted. If using a PUBLISHABLE API Key this may include a malformed security hash."

  final val notFoundCode = 404
  final val notFoundCodeDesc = "Resource could not be found."

  final val mediaErrorCode = 415
  final val mediaErrorCodeDesc = "An unexpected Content-Type was supplied with the request. This server accepts only 'Content-Type: application/json'."

  final val serverErrorCode = 500
  final val serverErrorCodeDesc = "Internal server error occurred - please contact support and provide uniqueId."

  // error objects
  final val errorObject = "Properties of an error generated during a request."
  final val errorResponseObject = "Response generated when one or more errors occur during a request."

  // error fields
  final val dataDesc = "List of errors that occurred during the request."
  final val errorStatusDesc = "HTTP status code."
  final val errorDescCodeDesc = "Descriptive error code."
  final val errorMessageDesc = "Human-readable message providing details of the error that occurred."
  final val errorParamDesc = "Parameter the error relates to, useful for displaying error messages on forms."
  final val errorUniqueIdDesc = "Uniquely identifying string for this error. Should be referenced when obtaining support."

  // error values
  final val genericError = "An internal server error occurred."

  // authentication notes
  final val userOrSecretOrPublishableAuthNote = "Can use any of user/password, publishable key or secret key authentication."
  final val userOrSecretAuthNote = "Can only use user/password or secret key authentication."
  final val userOrSecretAuthProviderOnlyNote = "Can only use user/password or secret key authentication, can only be accessed by providers."
  final val userAuthNote = "Can only use user/password authentication."

  // not implemented notes
  final val notYetImplementedNote = "This endpoint is NOT YET IMPLEMENTED. However, this API documentation may still be relied upon as the endpoint will come online soon."

  /**
   * Auth
   */
  object Auth {
    final val failedDesc = "Authorization failed."
  }

  /**
   * Keys
   */
  object Keys {
    // common
    final val keysResponse = "Keys Response."

    // endpoint
    final val endpointDesc = "Retrieve API keys."

    // read
    final val readDesc = endpointDesc
    final val readOKDesc = "API key retrieval succeeded."

    // refresh
    final val refreshDesc = "Deactivate all existing API keys, create new ones and retrieve them. WARNING: existing integrations will fail until updated."
    final val refreshOKDesc = "Successfully created new API keys."
    final val refreshErrorDesc = "Failed to create new API keys."

    // objects
    final val keysDesc = "List of API keys for the authenticated account."
    final val keyDesc = "Representation of an API key."

    // fields
    final val id = "id"
    final val idDesc = "Unique identifier of the API key."

    final val key = "apiKey"
    final val keyValueDesc = "API Key."

    final val active = "active"
    final val activeDesc = "Whether the API key is marked active. Inactive API keys will not be able to authenticate."

    final val createdAt = "createdAt"
    final val createdAtDesc = "When the API key was created. ISO_8601 datetime format ([YYYY]-[MM]-[DD]T[hh]:[mm]:[ss])."

  }

  /**
   * User
   */
  object User {
    // common
    final val userResponse = "User Response."

    // endpoint
    final val endpointDesc = "Create, update and retrieve users."

    // create
    final val createDesc = "Create a user."
    final val createRequestDesc = "User Creation Request Representation"
    final val createOKDesc = "User created successfully."
    final val createErrorDesc = "User creation failed."

    // update
    final val updateDesc = "Update a user."
    final val updateRequestDesc = "User Update Request Representation"
    final val updateOKDesc = "User update succeeded."
    final val updateErrorDesc = "User update failed."
    final val updateNotFoundDesc = "Specified user not found." // aliased across labels

    // read
    final val readDesc = "Retrieve a user."
    final val readMeDesc = "Retrieve my user."
    final val readOKDesc = "User retrieval succeeded."
    final val readNotFoundDesc = updateNotFoundDesc

    // reset initiate
    final val resetInitDesc = "Reset a user's password." // via an email containing a link that must be visited to confirm the reset.
    final val resetInitRequestDesc = "Reset User Password Request Representation"
    final val resetInitOKDesc = "Reset password initiation completed successfully."
    final val resetInitErrorDesc = "Reset password initiation failed."
    final val resetInitNotFoundDesc = updateNotFoundDesc

    // reset complete
    final val resetCompleteDesc = "Complete password reset." // process by visiting the confirmation link sent to a user's email address.
    final val resetCompleteFoundDesc = "Password reset was completed successfully, redirect to a login URI."
    final val resetCompleteErrorDesc = "Reset password completion failed."
    final val resetCompleteNotFoundDesc = updateNotFoundDesc

    // fields
    final val id = "id"
    final val idDesc = "Unique identifier of the user."

    final val email = "email"
    final val emailDesc = "Unique account identifier and email address for password recovery and notifications."
    final val emailUniqueErrorDesc = "Email must be a unique identifier and this email is already in use."

    final val password = "password"
    final val passwordDesc = "Account password will be securely stored and cannot be retrieved."

    final val active = "active"
    final val activeDesc = "Whether the user is marked active. Inactive users will not be able to login."

    final val createdAt = "createdAt"
    final val createdAtDesc = "When the user was created. ISO_8601 datetime format ([YYYY]-[MM]-[DD]T[hh]:[mm]:[ss])."

    final val resetCode = "c"
    final val resetCodeDesc = "Code supplied as part of a link in the reset email sent to user's email address."
  }

  /**
   * Shared field type strings
   */
  object Field {

    final val identifierLengthErrorDesc = "Identifier length must be between 0 and 255 characters inclusive."
    final val nameLengthErrorDesc = "Name length must be between 3 and 80 characters inclusive."
    final val emailLengthErrorDesc = "Email address must be between 5 and 255 characters inclusive."
    final val urlLengthErrorDesc = "URL must be between 5 and 1000 characters inclusive."
    final val urlMalformedErrorDesc = "URL must properly formed."

  }

}

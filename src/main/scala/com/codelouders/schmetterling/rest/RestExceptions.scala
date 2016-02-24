/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.rest

import spray.http.StatusCodes


/**
 * Base REST exceptions
 */

class RestException(val code: spray.http.StatusCode, val msg: String) extends Exception(msg)

case class EntityNotFound(override val msg: String) extends RestException(StatusCodes.NotFound, msg)

case class UpdateException(override val msg: String) extends RestException(StatusCodes.BadRequest, msg)

case class ServerException(override val msg: String) extends RestException(StatusCodes.InternalServerError, msg)

case class RestrictedAccessException(override val msg: String) extends RestException(StatusCodes.Unauthorized, msg)
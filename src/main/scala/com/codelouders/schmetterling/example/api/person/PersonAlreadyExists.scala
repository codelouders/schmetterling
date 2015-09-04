/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.person

import spray.http.StatusCodes

import com.codelouders.schmetterling.rest.RestException


class PersonAlreadyExists(msg: String) extends RestException(StatusCodes.Conflict, msg)

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.example.api.person

import com.codelouders.shmetterling.rest.RestException
import spray.http.StatusCodes

class PersonAlreadyExists(msg: String) extends RestException(StatusCodes.Conflict, msg)

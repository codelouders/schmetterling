/**
 * Created by WiktorT on 28/04/2015.
 *
 * Created on 28/04/2015
 */
package com.codelouders.schmetterling.auth.oauth2

import com.codelouders.schmetterling.entity.BaseEntity
import com.codelouders.schmetterling.rest.auth.AuthenticatedUser


case class OauthUser(id: Option[Int] = None, username: String, password: String) extends BaseEntity with AuthenticatedUser

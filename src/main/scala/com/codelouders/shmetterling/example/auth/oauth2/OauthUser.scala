/**
 * Created by WiktorT on 28/04/2015.
 *
 * Created on 28/04/2015
 */
package com.codelouders.shmetterling.example.auth.oauth2

import com.codelouders.shmetterling.entity.BaseEntity
import com.codelouders.shmetterling.rest.auth.AuthenticatedUser

case class OauthUser(id: Option[Int] = None, username: String, password: String) extends BaseEntity with AuthenticatedUser
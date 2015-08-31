package com.codelouders.shmetterling.example.auth.oauth2.provider

import com.codelouders.shmetterling.example.auth.oauth2.OauthUser

/**
 * Created by wiktort on 12/05/2015.
 *
 * Created on 12/05/2015
 */
trait AuthorizationProvider {
  def login(userToLogin: OauthUser): Option[OauthUser]
}

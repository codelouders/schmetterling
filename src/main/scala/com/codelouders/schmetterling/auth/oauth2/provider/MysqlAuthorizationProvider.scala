/**
 * Created by wiktort on 12/05/2015.
 *
 * Created on 12/05/2015
 */
package com.codelouders.schmetterling.auth.oauth2.provider

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.TableQuery

import com.codelouders.schmetterling.database.DatabaseAccess
import com.codelouders.schmetterling.auth.oauth2.{OauthUserT, OauthUser}
import com.codelouders.schmetterling.util.CryptoUtil


class MysqlAuthorizationProvider() extends AuthorizationProvider with DatabaseAccess{

  override def login(userToLogin: OauthUser): Option[OauthUser] = {
    val password = CryptoUtil.sha1(userToLogin.password.toCharArray.map{_.toByte})

    connectionPool withSession {
      implicit session =>
        TableQuery[OauthUserT].filter(item => item.username === userToLogin.username &&
          item.password === password).firstOption
    }
  }
}

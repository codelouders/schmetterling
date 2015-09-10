/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.schmetterling

import akka.actor.ActorSystem
import com.codelouders.schmetterling.database.DatabaseAccess

import com.codelouders.schmetterling.example.api.company.CompanyApiBuilder
import com.codelouders.schmetterling.example.api.person.PersonApiBuilder
import com.codelouders.schmetterling.auth.oauth2.{OauthUser, OauthUserDao, OauthAuthorization, OauthConfig}
import com.codelouders.schmetterling.auth.oauth2.provider.MysqlAuthorizationProvider
import com.codelouders.schmetterling.logger.{DebugLevel, ErrorLevel, FileLogger, ConsoleLogger}
import com.codelouders.schmetterling.rest.Rest
import com.codelouders.schmetterling.util.CryptoUtil


// Without authorization
object RestExampleApp extends App {

  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger, new FileLogger("c:\\temp\\rest-container.log", ErrorLevel)))
    .withPostApiInit({() => println("Run init function")}).start()
}

// With OAuth2 authorization
object RestWithOauthExampleApp extends App with DatabaseAccess {
  val oauthConfig = new OauthConfig(new MysqlAuthorizationProvider())

  val initFunction = {
    () => {
      Thread.sleep(1000)
      connectionPool withSession {
        implicit session =>
          val userDao = new OauthUserDao()
          userDao.getById(1) match {
            case None =>
              userDao.create(new OauthUser(Some(1), "admin", CryptoUtil.sha1("Nintendo64".map {
                _.toByte
              }.toArray)))
            case Some(_) =>
          }
      }
      ()
    }
  }

  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger, new FileLogger("c:\\temp\\rest-container.log", DebugLevel)),
    new OauthAuthorization(oauthConfig)).withPostApiInit(initFunction).start()
}

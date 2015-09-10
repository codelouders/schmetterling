/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.schmetterling

import akka.actor.ActorSystem

import com.codelouders.schmetterling.example.api.company.CompanyApiBuilder
import com.codelouders.schmetterling.example.api.person.PersonApiBuilder
import com.codelouders.schmetterling.auth.oauth2.{OauthAuthorization, OauthConfig}
import com.codelouders.schmetterling.auth.oauth2.provider.MysqlAuthorizationProvider
import com.codelouders.schmetterling.logger.{FileLogger, ConsoleLogger}
import com.codelouders.schmetterling.rest.Rest


// Without authorization
object RestExampleApp extends App {
  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger, new FileLogger("c:\\temp\\rest-container.log")))
    .withPostApiInit({() => println("Run init function")}).start()
}

// With OAuth2 authorization
object RestWithOauthExampleApp extends App {
  val oauthConfig = new OauthConfig(new MysqlAuthorizationProvider())

  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger), new OauthAuthorization(oauthConfig)).start()
}

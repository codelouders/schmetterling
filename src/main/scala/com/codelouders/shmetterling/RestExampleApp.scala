/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.shmetterling

import akka.actor.ActorSystem
import com.codelouders.shmetterling.example.api.company.CompanyApiBuilder
import com.codelouders.shmetterling.example.api.person.PersonApiBuilder
import com.codelouders.shmetterling.example.auth.oauth2.{OauthAuthorization, OauthConfig}
import com.codelouders.shmetterling.example.auth.oauth2.provider.MysqlAuthorizationProvider
import com.codelouders.shmetterling.logger.ConsoleLogger
import com.codelouders.shmetterling.rest.Rest


// Without authorization
object RestExampleApp extends App {
  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger)).start()
}


// With oauth 2 authorization
object RestWithOauthExampleApp extends App {
  val oauthConfig = new OauthConfig(new MysqlAuthorizationProvider())

  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger), new OauthAuthorization(oauthConfig)).start()
}

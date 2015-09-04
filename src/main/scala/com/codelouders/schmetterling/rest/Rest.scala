/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.rest

import scala.concurrent.duration._

import akka.actor.{ActorSystem, PoisonPill}
import akka.io.IO
import akka.util.Timeout

import com.typesafe.config.ConfigFactory

import spray.can.Http
import spray.can.server.UHttp

import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.{LoggingService, Logger}
import com.codelouders.schmetterling.rest.auth.{NoAuthorisation, Authorization}
import com.codelouders.schmetterling.websocket.WebSocketServer


/**
 * Rest service main class
 *
 * run start method on this obejct in order to start it up
 *
 * @param actorSystem - actor system in which rest service will be placed
 * @param listOfResourceApiBuilders - list of BaseResourceBuilder
 * @param loggers - list of loggers
 * @param authorization - authorisation method - default: NoAuthorisation
 */
class Rest(actorSystem: ActorSystem, listOfResourceApiBuilders: List[BaseResourceBuilder], loggers: List[Logger],
           authorization: Authorization = NoAuthorisation, postInit: () => Any = {() => Unit}) {


  // we need an ActorSystem to host our application in
  implicit val system = actorSystem

  def withPostApiInit(postInitFunction: () => Unit): Rest = {
    new Rest(actorSystem, listOfResourceApiBuilders, loggers, authorization, postInitFunction)
  }

  def start(): Unit = {

    println("Starting up...")

    // start up logger actor system and logger actor
    val conf = ConfigFactory.load()
    val logConf = conf.getConfig("logging")
    LoggingService.init(loggers, Some(logConf))

    authorization.init()

    // start up API service actor
    val apis = authorization.getAuthApiBuilder match {
      case Some(authResourceBuilder) =>
        authResourceBuilder :: listOfResourceApiBuilders
      case None => listOfResourceApiBuilders
    }

    val eventBus =  new SchmetteringEventBus
    val service = system.actorOf(ApiService.props(apis, authorization, eventBus), ApiService.ActorName)
    val server = system.actorOf(WebSocketServer.props(authorization, eventBus), WebSocketServer.Name)

    postInit()

    val runWebSocketServer = conf.getBoolean("websocket.run")

    runWebSocketServer match {
      case true =>
        println("Websocket is starting...")
        implicit val timeout = Timeout(5.seconds)
        // start a new HTTP server on port 8080 with our service actor as the handler
        IO(UHttp) ! Http.Bind(server, "localhost", port = 8082)
      case false =>
        println("Websocket is turn off...")
    }

    println("Rest is starting...")
    // SPRAY WORKAROUND: Must me killed before starting rest server because of actor naming collision
    system.actorSelection("/user/IO-HTTP") ! PoisonPill
    Thread.sleep(1000)
    IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
  }

  def stop(): Unit = {
    println("Shouting down...")
    system.shutdown()
    system.awaitTermination()
  }
}

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.schmetterling.websocket

import akka.actor.{Props, Actor}

import spray.can.Http

import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.auth.{Authorization, RestApiUser}


class WebSocketServer(authorization: Authorization, eventBus: SchmetteringEventBus) extends Actor with Logging {

  def receive = {

    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case req@Http.Connected(remoteAddress, localAddress) =>

      val serverConnection = sender()
      // should be match with a session/user - this way it will be possible to push message only to specific user
      val conn = context.actorOf(WebSocketWorker.props(serverConnection, authorization, eventBus))
      serverConnection ! Http.Register(conn)

  }

  override val logTag: String = getClass.getName
}

object WebSocketServer {
  val Name = "websocket"

  def props(authorization: Authorization, eventBus: SchmetteringEventBus) = {
    Props(classOf[WebSocketServer], authorization, eventBus)
  }
}

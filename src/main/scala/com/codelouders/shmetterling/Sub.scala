package com.codelouders.shmetterling

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.codelouders.shmetterling.util.JsonUtil
import com.codelouders.shmetterling.websocket._


/**
 * Created by WiktorT on 01/09/2015.
 *
 * Created on 01/09/2015
 */
class Sub(testEventBus: TestEventBus) extends Actor {

  override def preStart() = {
    testEventBus.subscribe(self, EntityChanged)
  }

  override def receive: Receive = {
    case PubSubNotification(_, cm@CreateEntityNotification(entityType, uri, created)) =>
      println(cm)
      println(JsonUtil.serialize(cm))
      println(entityType)
      println(uri)
      println(created)

    case PubSubNotification(_, a: PubSubEntityChangeMessage) =>
      println("other")
      println(a)
      println(JsonUtil.serialize(a))
  }
}

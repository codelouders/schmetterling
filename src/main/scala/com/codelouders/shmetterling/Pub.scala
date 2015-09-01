package com.codelouders.shmetterling

import akka.actor.Actor
import com.codelouders.shmetterling.example.api.person._
import com.codelouders.shmetterling.websocket._


/**
  * Created by WiktorT on 01/09/2015.
  *
  * Created on 01/09/2015
  */
class Pub(testEventBus: TestEventBus) extends Actor {
   override def receive: Receive = {
     case "c" =>
       val msg = CreateEntityNotification("person", "http://localhost/route/123", Person(Some(123), "test name", "test lastname"))
       testEventBus.publish(PubSubNotification(EntityChanged, msg))

     case "d" =>
       val msg = DeleteEntityNotification("person", 123)
       testEventBus.publish(PubSubNotification(EntityChanged, msg))
   }
 }

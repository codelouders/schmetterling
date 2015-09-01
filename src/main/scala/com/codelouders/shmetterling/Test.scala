package com.codelouders.shmetterling

import akka.actor.{Props, ActorSystem}

/**
 * Created by WiktorT on 01/09/2015.
 *
 * Created on 01/09/2015
 */
object Test extends App {

  val system = ActorSystem("MySystem")
  val appActorEventBus = new TestEventBus

  val pub = system.actorOf(Props(classOf[Pub], appActorEventBus))
  system.actorOf(Props(classOf[Sub], appActorEventBus))

  Thread.sleep(1000)

  pub ! "c"

  Thread.sleep(1000)

  pub ! "d"


  Thread.sleep(1000000)
}

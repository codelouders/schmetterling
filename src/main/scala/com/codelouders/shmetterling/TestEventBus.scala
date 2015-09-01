package com.codelouders.shmetterling

import akka.event.{LookupClassification, ActorEventBus}
import com.codelouders.shmetterling.websocket.{PubSubMessageCategory, PubSubNotification}
import spray.can.Http.MessageEvent

/**
 * Created by WiktorT on 01/09/2015.
 *
 * Created on 01/09/2015
 */


class TestEventBus extends ActorEventBus with LookupClassification {
  type Event = PubSubNotification
  type Classifier = PubSubMessageCategory




  protected def classify(event: Event): Classifier = {
    event.eventKind
  }


  protected def publish(event: Event, subscriber: Subscriber): Unit={
    subscriber ! event
  }

  override protected def mapSize(): Int = 2
}

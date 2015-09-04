/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.events

import akka.event.{LookupClassification, ActorEventBus}


class SchmetteringEventBus  extends ActorEventBus with LookupClassification {
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

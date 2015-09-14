/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-09-02
 */
package com.codelouders.schmetterling.events.notification

import akka.actor.ActorContext
import com.codelouders.schmetterling.events.Notifications
import com.codelouders.schmetterling.rest.auth.RestApiUser

/**
 * Allows to send push messages via websocket
 */
trait EntityChangedNotifications extends Notifications {

  /**
   * Pushes message to all open sockets
   * @param messageToPush - message to be pushed
   * @param context - akka actor context
   */
  def publishAll(messageToPush: PubSubEntityChangeMessage)(implicit context: ActorContext, restApiUser: RestApiUser) {
    publish(EntityChanged, messageToPush)
  }

}



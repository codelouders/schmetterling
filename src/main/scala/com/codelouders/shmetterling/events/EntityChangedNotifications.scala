/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-09-02
 */


package com.codelouders.shmetterling.events

import akka.actor.ActorContext
import com.codelouders.shmetterling.rest.auth.RestApiUser
import com.codelouders.shmetterling.util.JsonUtil

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

  /**
   * Pushes message to specific user
   *
   * @param user - user to which message should be pushed
   *
   * @param messageToPush - message to be pushed
   * @param context - akka actor context
   */
  def publishToUser(user: RestApiUser, messageToPush: NotifyUserMessage)(implicit context: ActorContext, restApiUser: RestApiUser) {
    publish(EntityChanged, messageToPush)
  }
}



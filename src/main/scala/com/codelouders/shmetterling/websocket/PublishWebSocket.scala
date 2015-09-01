/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */


package com.codelouders.shmetterling.websocket

import akka.actor.ActorContext
import com.codelouders.shmetterling.rest.auth.RestApiUser
import com.codelouders.shmetterling.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Allows to send push messages via websocket
 */
trait PublishWebSocket {

  def

  /**
   * Pushes message to all open sockets
   * @param messageToPush - message to be pushed
   * @param context - akka actor context
   */
  def publishAll(messageToPush: PubSubMessage)(implicit context: ActorContext) {
    val websocket = context.actorSelection(s"/user/websocket")
    websocket ! Push(JsonUtil.serialize(messageToPush))
  }

  /**
   * Pushes message to specific user
   *
   * @param user - user to which message should be pushed
   *
   * @param messageToPush - message to be pushed
   * @param context - akka actor context
   */
  def publishToUser(user: RestApiUser, messageToPush: PubSubMessage)(implicit context: ActorContext) {
    val websocket = context.actorSelection(s"/user/websocket")
    websocket ! PushToUser(user, JsonUtil.serialize(messageToPush))
  }
}


case class PubSubNotification(eventKind: PubSubMessageCategory, message: PubSubMessage)

sealed abstract class PubSubMessageCategory(kind: String)

object EventKind {
  val EntityChanged = "entityChanged"
}

case object EntityChanged extends PubSubMessageCategory(EventKind.EntityChanged)


sealed trait PubSubMessage
sealed abstract class PubSubEntityChangeMessage(val operation: OperationType) extends PubSubMessage

sealed trait OperationType {
  @JsonValue
  def operation: String
}

case object CreateOperation extends OperationType {
  def operation = "create"
}
case object DeleteOperation extends OperationType {
  def operation = "delete"
}
case object UpdateOperation extends OperationType {
  def operation = "update"
}

case class CreateEntityNotification(entityType: String, uri: String, createdEntity: Any) extends PubSubEntityChangeMessage(CreateOperation)
case class UpdateEntityNotification(entityType: String, uri: String, updatedEntity: Any) extends PubSubEntityChangeMessage(UpdateOperation)
case class DeleteEntityNotification(entityType: String, id: Int) extends PubSubEntityChangeMessage(DeleteOperation)

package com.codelouders.schmetterling.websocket.notification

import com.codelouders.schmetterling.events.{PubSubMessage, OperationType}
import com.codelouders.schmetterling.rest.auth.RestApiUser

/**
 * Created by WiktorT on 14/09/2015.
 *
 * Created on 14/09/2015
 */
sealed abstract class NotifyUserMessage(val user: RestApiUser, val operation: OperationType) extends PubSubMessage

case class CreateEntityUserNotification(userToNotify: RestApiUser, entityType: String, uri: String, createdEntity: Any)
  extends NotifyUserMessage(userToNotify, CreateOperation)

case class UpdateEntityUserNotification(userToNotify: RestApiUser, entityType: String, uri: String, updatedEntity: Any)
  extends NotifyUserMessage(userToNotify, UpdateOperation)

case class DeleteEntityUserNotification(userToNotify: RestApiUser, entityType: String, id: Int)
  extends NotifyUserMessage(userToNotify, DeleteOperation)


object NotifyUserOperationType {
  val Create = "create"
  val Update = "update"
  val Delete = "delete"
}

case object CreateOperation extends OperationType {
  def operation = NotifyUserOperationType.Create
}

case object UpdateOperation extends OperationType {
  def operation = NotifyUserOperationType.Update
}

case object DeleteOperation extends OperationType {
  def operation = NotifyUserOperationType.Delete
}

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-09-02
 */
package com.codelouders.shmetterling.events

import com.codelouders.shmetterling.rest.auth.RestApiUser
import com.fasterxml.jackson.annotation.JsonValue

case class PubSubNotification(eventKind: PubSubMessageCategory, message: PubSubMessage)(implicit restApiUser: RestApiUser)

sealed trait PubSubMessageCategory {
  @JsonValue
  def kind: String
}

object PubSubMessageCategory {
  val EntityChanged = "entityChanged"
}

case object EntityChanged extends PubSubMessageCategory {
  def kind = PubSubMessageCategory.EntityChanged
}


sealed trait PubSubMessage
sealed abstract class PubSubEntityChangeMessage(val operation: OperationType) extends PubSubMessage
sealed abstract class NotifyUserMessage(val user: RestApiUser, val operation: OperationType) extends PubSubMessage

sealed trait OperationType {
  @JsonValue
  def operation: String
}

object OperationType {
  val Create = "create"
  val Update = "update"
  val Delete = "delete"
}

case object CreateOperation extends OperationType {
  def operation = OperationType.Create
}

case object UpdateOperation extends OperationType {
  def operation = OperationType.Update
}

case object DeleteOperation extends OperationType {
  def operation = OperationType.Delete
}

case class CreateEntityNotification(entityType: String, uri: String, createdEntity: Any) extends PubSubEntityChangeMessage(CreateOperation)
case class UpdateEntityNotification(entityType: String, uri: String, updatedEntity: Any) extends PubSubEntityChangeMessage(UpdateOperation)
case class DeleteEntityNotification(entityType: String, id: Int) extends PubSubEntityChangeMessage(DeleteOperation)

case class CreateEntityUserNotification(userToNotify: RestApiUser, entityType: String, uri: String, createdEntity: Any)
  extends NotifyUserMessage(userToNotify, CreateOperation)

case class UpdateEntityUserNotification(userToNotify: RestApiUser, entityType: String, uri: String, updatedEntity: Any)
  extends NotifyUserMessage(userToNotify, UpdateOperation)

case class DeleteEntityUserNotification(userToNotify: RestApiUser, entityType: String, id: Int)
  extends NotifyUserMessage(userToNotify, DeleteOperation)

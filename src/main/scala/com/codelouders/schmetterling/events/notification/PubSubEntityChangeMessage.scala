/**
 * Created by WiktorT on 14/09/2015.
 *
 * Created on 14/09/2015
 */
package com.codelouders.schmetterling.events.notification

import com.codelouders.schmetterling.events.{OperationType, PubSubMessage}

sealed abstract class PubSubEntityChangeMessage(val operation: OperationType) extends PubSubMessage

case class CreateEntityNotification(entityType: String, uri: String, createdEntity: Any) extends PubSubEntityChangeMessage(CreateOperation)
case class UpdateEntityNotification(entityType: String, uri: String, updatedEntity: Any) extends PubSubEntityChangeMessage(UpdateOperation)
case class DeleteEntityNotification(entityType: String, id: Int) extends PubSubEntityChangeMessage(DeleteOperation)

object EntityChangeOperationType {
  val Create = "create"
  val Update = "update"
  val Delete = "delete"
}

case object CreateOperation extends OperationType {
  def operation = EntityChangeOperationType.Create
}

case object UpdateOperation extends OperationType {
  def operation = EntityChangeOperationType.Update
}

case object DeleteOperation extends OperationType {
  def operation = EntityChangeOperationType.Delete
}

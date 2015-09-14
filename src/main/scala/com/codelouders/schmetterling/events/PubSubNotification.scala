/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-09-02
 */
package com.codelouders.schmetterling.events

import com.fasterxml.jackson.annotation.JsonValue

import com.codelouders.schmetterling.rest.auth.RestApiUser


case class PubSubNotification(eventKind: PubSubMessageCategory, message: PubSubMessage)(implicit restApiUser: RestApiUser)

trait PubSubMessageCategory {

  @JsonValue
  def kind: String
}

trait OperationType {
  @JsonValue
  def operation: String
}

trait PubSubMessage {
  val operation: OperationType
}

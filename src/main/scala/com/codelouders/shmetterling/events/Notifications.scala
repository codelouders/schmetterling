/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-09-02
 */

package com.codelouders.shmetterling.events

import com.codelouders.shmetterling.rest.auth.RestApiUser


trait Notifications {
  def eventBus(): SchmetteringEventBus

  def publish(category: PubSubMessageCategory, messageToPush: PubSubMessage)(implicit restApiUser: RestApiUser) = {
    eventBus().publish(PubSubNotification(category, messageToPush))
  }
}

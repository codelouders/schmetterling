/**
 * Created by WiktorT on 14/09/2015.
 *
 * Created on 14/09/2015
 */
package com.codelouders.schmetterling.events.notification

import com.codelouders.schmetterling.events.PubSubMessageCategory


case object EntityChanged extends PubSubMessageCategory {

  val EntityChanged = "entityChanged"

  def kind = EntityChanged
}

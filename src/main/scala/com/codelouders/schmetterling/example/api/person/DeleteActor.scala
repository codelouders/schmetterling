/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.person

import akka.actor.{Props, Actor}
import com.codelouders.schmetterling.events.notification.{DeleteEntityNotification, EntityChangedNotifications}

import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.EntityNotFound
import com.codelouders.schmetterling.rest.auth.RestApiUser


case class DeleteMessage(ctx: RequestContext, personId: Int)(implicit val loggedUser: RestApiUser)

case class DeleteResult(deleted: Boolean)

/**
 * Actor handling delete message
 */
class DeleteActor(personDao: PersonDao, override val eventBus: SchmetteringEventBus) extends Actor
with EntityChangedNotifications with Logging {

  override def receive: Receive = {
    case msg@DeleteMessage(ctx, personId) =>
      L.debug(s"deleting person $personId")
      implicit val user = msg.loggedUser
      val count = personDao.deleteById(personId)
      if (count == 1){
        ctx.complete("")
      } else {
        ctx.complete(new EntityNotFound("Trying to delete non existent entity"))
      }
      publishAll(DeleteEntityNotification(ResourceName, personId))

  }

  override val logTag: String = getClass.getName
}

object DeleteActor {
  val Name = s"${ResourceName}DeleteRouter"
  def props(personDao: PersonDao, eventBus: SchmetteringEventBus) = Props(classOf[DeleteActor], personDao, eventBus)
}

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.person

import akka.actor.{Props, Actor}
import com.codelouders.schmetterling.events.notification.{CreateEntityNotification, EntityChangedNotifications}

import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

import com.codelouders.schmetterling.entity.EntityHelper
import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.auth.RestApiUser
import com.codelouders.schmetterling.util.HttpRequestContextUtils


case class CreateMessage(ctx: RequestContext, person: Person)(implicit val loggedUser: RestApiUser)

/**
 * Actor handling person create message
 */
class CreateActor(personDao: PersonDao, override val eventBus: SchmetteringEventBus) extends Actor with Logging
with EntityChangedNotifications with HttpRequestContextUtils with EntityHelper {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case msg@CreateMessage(ctx, person) =>
      implicit val user = msg.loggedUser
      try {
        val addedPerson = person.copy(id = Some(personDao.create(person)))
        ctx.complete(addedPerson)
        publishAll(CreateEntityNotification(ResourceName, entityUri(getRequestUri(ctx), addedPerson), addedPerson))
        L.debug(s"Person create success")
      } catch {
        case e: Exception =>
          L.error(s"Ups cannot create person: ${e.getMessage}", e)
          ctx.complete(e)
      }
  }
}

object CreateActor {
  val Name = s"${ResourceName}CreateRouter"
  def props(personDao: PersonDao, eventBus: SchmetteringEventBus) = Props(classOf[CreateActor], personDao, eventBus)
}

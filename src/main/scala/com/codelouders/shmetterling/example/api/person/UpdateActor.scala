/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.shmetterling.example.api.person

import akka.actor.{Props, Actor}
import com.codelouders.shmetterling.entity.JsonNotation
import com.codelouders.shmetterling.events.{UpdateEntityNotification, EntityChangedNotifications, SchmetteringEventBus}
import com.codelouders.shmetterling.logger.Logging
import com.codelouders.shmetterling.rest.auth.RestApiUser
import com.codelouders.shmetterling.rest.{EntityNotFound, UpdateException}
import com.codelouders.shmetterling.util.HttpRequestContextUtils
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

case class PutMessage(ctx: RequestContext, user: Person)(implicit val loggedUser: RestApiUser)
case class PatchMessage(ctx: RequestContext, patch: List[JsonNotation], userId: Int)(implicit val loggedUser: RestApiUser)

/**
 * Actor handling update messages
 */
class UpdateActor(personDao: PersonDao, override val eventBus: SchmetteringEventBus) extends Actor
with EntityChangedNotifications with Logging with HttpRequestContextUtils {


  override def receive: Receive = {

    //handling put message
    case msg@PutMessage(ctx, person) =>
      implicit val user = msg.loggedUser
      val updated = personDao.update(person)
      if (updated == 1) {
        ctx.complete(person)
        publishAll(UpdateEntityNotification(ResourceName, getRequestUri(ctx), person))
      } else {
        ctx.complete(EntityNotFound(s"Not found person id ${person.id}"))
      }


    //handling patch message
    case msg@PatchMessage(ctx, patch, id) =>
      try{
        implicit val user = msg.loggedUser
        val updated = personDao.patch(patch, id)
        if (updated.forall(_ == 1)) {
          val person = personDao.getById(id)
          ctx.complete(person)
          publishAll(UpdateEntityNotification(ResourceName, getRequestUri(ctx), person))
        } else {
          ctx.complete(EntityNotFound(s"Not found person id $id"))
        }
      } catch {
        case e: UpdateException =>
          ctx.complete(e)
      }
  }

  override val logTag: String = getClass.getName
}

object UpdateActor {
  val Name = s"${ResourceName}PutRouter"
  def props(personDao: PersonDao, eventBus: SchmetteringEventBus) = Props(classOf[UpdateActor], personDao, eventBus)
}

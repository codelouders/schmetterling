/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.shmetterling.example.api.person

import akka.actor.{Props, Actor}
import com.codelouders.shmetterling.entity.JsonNotation
import com.codelouders.shmetterling.logger.Logging
import com.codelouders.shmetterling.rest.{EntityNotFound, UpdateException}
import com.codelouders.shmetterling.util.HttpRequestContextUtils
import com.codelouders.shmetterling.websocket.{UpdateEntityNotification, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

case class PutMessage(ctx: RequestContext, user: Person)
case class PatchMessage(ctx: RequestContext, patch: List[JsonNotation], userId: Int)

/**
 * Actor handling update messages
 */
class UpdateActor(personDao: PersonDao) extends Actor with PublishWebSocket with Logging with HttpRequestContextUtils {


  override def receive: Receive = {

    //handling put message
    case PutMessage(ctx, person) =>

      val updated = personDao.update(person)
      if (updated == 1) {
        ctx.complete(person)
        publishAll(UpdateEntityNotification(ResourceName, getRequestUri(ctx), person))
      } else {
        ctx.complete(EntityNotFound(s"Not found person id ${person.id}"))
      }


    //handling patch message
    case PatchMessage(ctx, patch, id) =>
    try{
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
  def props(personDao: PersonDao) = Props(classOf[UpdateActor], personDao)
}

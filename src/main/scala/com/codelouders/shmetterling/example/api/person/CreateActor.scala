/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.example.api.person

import akka.actor.{Props, Actor}
import com.codelouders.shmetterling.entity.EntityHelper
import com.codelouders.shmetterling.logger.Logging
import com.codelouders.shmetterling.util.HttpRequestContextUtils
import com.codelouders.shmetterling.websocket.{CreateEntityNotification, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

case class CreateMessage(ctx: RequestContext, person: Person)

/**
 * Actor handling person create message
 */
class CreateActor(personDao: PersonDao) extends Actor with Logging with PublishWebSocket with HttpRequestContextUtils with EntityHelper {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case CreateMessage(ctx, person) =>

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
  def props(personDao: PersonDao) = Props(classOf[CreateActor], personDao)
}

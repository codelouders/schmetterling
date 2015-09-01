/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.example.api.company

import akka.actor.{Props, Actor}
import com.codelouders.shmetterling.entity.JsonNotation
import com.codelouders.shmetterling.events.{UpdateEntityNotification, EntityChangedNotifications, SchmetteringEventBus}
import com.codelouders.shmetterling.logger.Logging
import com.codelouders.shmetterling.rest.auth.RestApiUser
import com.codelouders.shmetterling.rest.{EntityNotFound, UpdateException}
import com.codelouders.shmetterling.util.HttpRequestContextUtils
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class PutMessage(ctx: RequestContext, company: Company)(implicit val loggedUser: RestApiUser)
case class PatchMessage(ctx: RequestContext, patch: List[JsonNotation], companyId: Int)(implicit val loggedUser: RestApiUser)

/**
 * Actor handling update messages
 */
class UpdateActor(companyDao: CompanyDao, override val eventBus: SchmetteringEventBus) extends Actor with EntityChangedNotifications
with Logging with HttpRequestContextUtils {


  override def receive: Receive = {

    //handling put message
    case msg@PutMessage(ctx, company) =>
      implicit val user = msg.loggedUser
      val updated = companyDao.update(company)
      if (updated == 1) {
        ctx.complete(company)
        publishAll(UpdateEntityNotification(ResourceName, getRequestUri(ctx), company))
      } else {
        ctx.complete(EntityNotFound(s"Not found company id ${company.id}"))
      }

    case msg@PatchMessage(ctx, patch, id) =>
      try {
        implicit val user = msg.loggedUser
        val updated = companyDao.patch(patch, id)
        if (updated.forall(_ == 1)) {
          val company = companyDao.getById(id)
          ctx.complete(company)
          publishAll(UpdateEntityNotification(ResourceName, getRequestUri(ctx), company))
        } else {
          ctx.complete(EntityNotFound(s"Not found company id $id"))
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
  def props(companyDao: CompanyDao, eventBus: SchmetteringEventBus) = Props(classOf[UpdateActor], companyDao, eventBus)
}

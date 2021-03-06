/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.company

import akka.actor.{Props, Actor}
import com.codelouders.schmetterling.events.notification.{EntityChangedNotifications, CreateEntityNotification}

import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

import com.codelouders.schmetterling.entity.EntityHelper
import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.auth.RestApiUser
import com.codelouders.schmetterling.util.HttpRequestContextUtils


case class CreateMessage(ctx: RequestContext, person: Company)(implicit val loggedUser: RestApiUser)


/**
 * Actor handling person create message
 */
class CreateActor(companyDao: CompanyDao, override val eventBus: SchmetteringEventBus) extends Actor
with Logging with EntityChangedNotifications with HttpRequestContextUtils with EntityHelper  {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case cm@CreateMessage(ctx, company) =>
      try {
        implicit val user = cm.loggedUser
        val added = company.copy(id = Some(companyDao.create(company)))
        ctx.complete(added)
        publishAll(CreateEntityNotification(ResourceName, entityUri(getRequestUri(ctx), added), added))
        L.debug(s"Company create success")
      } catch {
        case e: Exception =>
          L.error(s"Ups cannot create company: ${e.getMessage}", e)
          ctx.complete(e)
      }
  }
}

object CreateActor {
  val Name = s"${ResourceName}CreateRouter"
  def props(companyDao: CompanyDao, eventBus: SchmetteringEventBus) = Props(classOf[CreateActor], companyDao, eventBus)
}

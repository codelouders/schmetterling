/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.company

import akka.actor.{Props, Actor}
import com.codelouders.schmetterling.events.notification.EntityChangedNotifications

import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.auth.RestApiUser


case class GetMessage(ctx: RequestContext, userId: Option[Int])(implicit logged: RestApiUser)


/**
 * Actor handling person get message
 */
class GetActor(companyDao: CompanyDao, override val eventBus: SchmetteringEventBus) extends Actor
with Logging with EntityChangedNotifications {

  override val logTag: String = getClass.getName

  override def receive: Receive = {

    // get all persons
    case GetMessage(ctx, None) =>
      L.info("Getting all companies")
      ctx.complete(companyDao.getAll)


    // get person by id
    case GetMessage(ctx, Some(id)) =>
      L.info(s"Getting company id = $id")
      val localCtx = ctx
      localCtx.complete(companyDao.getById(id))
  }
}

object GetActor {
  val Name = s"${ResourceName}GetRouter"
  def props(companyDao: CompanyDao, eventBus: SchmetteringEventBus) = Props(classOf[GetActor], companyDao, eventBus)
}

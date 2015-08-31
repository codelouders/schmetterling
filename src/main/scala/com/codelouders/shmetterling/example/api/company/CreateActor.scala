/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.example.api.company

import akka.actor.{Props, Actor}
import com.codelouders.shmetterling.entity.EntityHelper
import com.codelouders.shmetterling.logger.Logging
import com.codelouders.shmetterling.rest.auth.RestApiUser
import com.codelouders.shmetterling.util.HttpRequestContextUtils
import com.codelouders.shmetterling.websocket.{CreatePublishMessage, PublishWebSocket}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class CreateMessage(ctx: RequestContext, person: Company)(implicit val loggedUser: RestApiUser)


/**
 * Actor handling person create message
 */
class CreateActor(companyDao: CompanyDao) extends Actor with Logging with PublishWebSocket with HttpRequestContextUtils with EntityHelper  {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case cm@CreateMessage(ctx, company) =>
      try {
        val added = company.copy(id = Some(companyDao.create(company)))
        ctx.complete(added)
        publishAll(CreatePublishMessage(ResourceName, entityUri(getRequestUri(ctx), added), added))
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
  def props(companyDao: CompanyDao) = Props(classOf[CreateActor], companyDao)
}

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.schmetterling.rest

import akka.actor._

import spray.routing._
import spray.json.DefaultJsonProtocol._
import spray.routing.authentication._

import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.auth.Authorization


/**
 * Main Api service class
 */
class ApiService(availableApis: List[BaseResourceBuilder], authorization: Authorization, eventBus: SchmetteringEventBus)
  extends Actor with HttpServiceBase with Logging {

  val apis = availableApis.map{_.create(context, authorization, eventBus)}

  lazy val routing: Route = apis.foldLeft[Route](null)((a,b) => if (a == null) b.apiRoute() else {a ~ b.apiRoute()})

  override val logTag: String = getClass.getName

  override def receive = runRoute(handleExceptions(new RestExceptionHandler().exceptionHandler){routing})

}

object ApiService {
  val ActorName = "api-root"

  def props(availableApis: List[BaseResourceBuilder],  authorization: Authorization, eventBus: SchmetteringEventBus) = {
    Props(classOf[ApiService], availableApis, authorization, eventBus)
  }
}

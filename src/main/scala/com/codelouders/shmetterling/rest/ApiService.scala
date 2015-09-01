/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.shmetterling.rest

import akka.actor._
import com.codelouders.shmetterling.events.SchmetteringEventBus
import com.codelouders.shmetterling.logger.Logging
import com.codelouders.shmetterling.rest.auth.Authorization
import spray.routing._
import spray.json.DefaultJsonProtocol._
import spray.routing.authentication._

/**
 * Main Api service class
 */
class ApiService(availableApis: List[BaseResourceBuilder], authorization: Authorization, eventBus: SchmetteringEventBus)
  extends Actor with HttpServiceBase with Logging {

  val apis = availableApis.map{_.create(context, authorization, eventBus)}
  apis.foreach(_.init())

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

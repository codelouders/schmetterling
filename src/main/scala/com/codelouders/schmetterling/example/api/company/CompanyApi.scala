/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.company

import akka.actor.ActorContext
import akka.routing.RoundRobinPool

import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import com.codelouders.schmetterling.entity.JsonNotation
import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.{BaseResourceBuilder, BaseResource}
import com.codelouders.schmetterling.rest.auth.{Authorization, RestApiUser}

import scala.concurrent.ExecutionContext


/**
 * Company API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
class CompanyApi(val actorContext: ActorContext, companyDao: CompanyDao, override val authorization: Authorization,
                 eventBus: SchmetteringEventBus)
  extends BaseResource with Logging {


  /**
   * Handler val names must be unique in the system - all
   */

  private val companyCreateHandler = actorContext.actorOf(RoundRobinPool(2).props(CreateActor.props(companyDao, eventBus)), CreateActor.Name)
  private val companyPutHandler = actorContext.actorOf(RoundRobinPool(5).props(UpdateActor.props(companyDao, eventBus)), UpdateActor.Name)
  private val companyGetHandler = actorContext.actorOf(RoundRobinPool(20).props(GetActor.props(companyDao, eventBus)), GetActor.Name)
  private val companyDeleteHandler = actorContext.actorOf(RoundRobinPool(2).props(DeleteActor.props(companyDao, eventBus)), DeleteActor.Name)

  override val logTag: String = getClass.getName

  override protected def getResourceName: String = {
    ResourceName
  }

  override def authorizedResource = true

  override def route(implicit userAuth: RestApiUser) = {

    pathEnd {
      get {
        ctx => companyGetHandler ! GetMessage(ctx, None)
      } ~
        post {
          entity(as[Company]) {
            company =>
              ctx => companyCreateHandler ! CreateMessage(ctx, company)
          }
        }
    } ~
    pathPrefix(IntNumber) {
      entityId => {
        pathEnd {
          get {
            ctx => companyGetHandler ! GetMessage(ctx, Some(entityId))
          } ~ put {
            entity(as[Company]) { entity =>
              ctx => companyPutHandler ! PutMessage(ctx, entity.copy(id = Some(entityId)))
            }
          } ~ delete {
            ctx => companyDeleteHandler ! DeleteMessage(ctx, entityId)
          } ~ patch {
            entity(as[List[JsonNotation]]) { patch =>
              ctx => companyPutHandler ! PatchMessage(ctx, patch, entityId)
            }
          }
        }
      }
    }

  }

}

class CompanyApiBuilder extends BaseResourceBuilder {

  val companyDao = new CompanyDao

  override def create(actorContext: ActorContext, authorization: Authorization, eventBus: SchmetteringEventBus): BaseResource = {
    new CompanyApi(actorContext, companyDao, authorization, eventBus)
  }

  override def init()(implicit ec:ExecutionContext) = {
    companyDao.initTable()
  }
}

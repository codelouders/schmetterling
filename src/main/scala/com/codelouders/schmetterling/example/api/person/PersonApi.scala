/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.person

import akka.actor.ActorContext
import akka.routing.RoundRobinPool

import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import com.codelouders.schmetterling.entity.JsonNotation
import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.{BaseResourceBuilder, BaseResource}
import com.codelouders.schmetterling.rest.auth.{RestApiUser, Authorization}

import scala.concurrent.ExecutionContext


/**
 * Person API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 *
 */
class PersonApi(val actorContext: ActorContext, personDao: PersonDao, override val authorization: Authorization,
                eventBus: SchmetteringEventBus) extends BaseResource with Logging {

  val personCreateHandler = actorContext.actorOf(RoundRobinPool(2).props(CreateActor.props(personDao, eventBus)), CreateActor.Name)
  val personPutHandler = actorContext.actorOf(RoundRobinPool(5).props(UpdateActor.props(personDao, eventBus)), UpdateActor.Name)
  val personGetHandler = actorContext.actorOf(RoundRobinPool(20).props(GetActor.props(personDao, eventBus)), GetActor.Name)
  val personDeleteHandler = actorContext.actorOf(RoundRobinPool(2).props(DeleteActor.props(personDao, eventBus)), DeleteActor.Name)

  override val logTag: String = getClass.getName

  override protected def getResourceName: String = {
    ResourceName
  }

  override def authorizedResource = false

  override def route(implicit restApiUser: RestApiUser) =

    pathEnd {
      get {
        ctx => personGetHandler ! GetMessage(ctx, None)
      } ~
      post {
        entity(as[Person]) {
          entity =>
            ctx => personCreateHandler ! CreateMessage(ctx, entity)
        }
      }
    } ~
    pathPrefix (IntNumber){
      entityId => {
        pathEnd {
          get {
            ctx => personGetHandler ! GetMessage(ctx, Some(entityId))
          } ~ put {
            entity(as[Person]) { entity =>
              ctx => personPutHandler ! PutMessage(ctx, entity.copy(id = Some(entityId)))
            }
          } ~ delete {
            ctx => personDeleteHandler ! DeleteMessage(ctx, entityId)
          } ~ patch {
            entity(as[List[JsonNotation]]) { patch =>
              ctx => personPutHandler ! PatchMessage(ctx, patch, entityId)
            }
          }
        }
      }
    }
}

class PersonApiBuilder extends BaseResourceBuilder {

  private val personDao = new PersonDao

  override def create(actorContext: ActorContext, authorization: Authorization, eventBus: SchmetteringEventBus): BaseResource = {
    new PersonApi(actorContext, personDao, authorization, eventBus)
  }

  override def init()(implicit ec:ExecutionContext) = {
    personDao.initTable()
  }
}
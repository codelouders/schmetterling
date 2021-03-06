/**
 * Created by wiktort on 19/08/2015.
 *
 * Created on 19/08/2015
 */
package com.codelouders.schmetterling.rest

import akka.actor.ActorContext

import spray.json.DefaultJsonProtocol._
import spray.routing._

import com.codelouders.schmetterling.entity.JsonNotation
import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.rest.auth.{NoAuthorisation, Authorization, RestApiUser}

import scala.concurrent.ExecutionContext


/**
 * Base class for all resources api available via REST
 *
 * implement:
 * route - To specify available route for resource
 * authorisedResource - to specify if accessing resource should be authorized
 *
 */
trait BaseResource extends HttpServiceBase {

  val actorContext: ActorContext
  implicit val ec = actorContext.dispatcher

  /**
   * Patch operation json notation object marshal/unmarshal format
   */
  implicit val PatchJsonNotationFormat = jsonFormat3(JsonNotation)

  def authorizedResource: Boolean

  protected def getResourceName: String

  /**
   * Route for resource
   *
   */
  def route(implicit user: RestApiUser): Route

  final private[rest] def apiRoute() = pathPrefix(getResourceName) {auth { user => route(user) }}

  /**
   * Authorization
   */
  def authorization: Authorization

  final private def auth: Directive1[RestApiUser] = {
    val authenticator = authorizedResource match {
      case true => authorization.authenticator
      case false => NoAuthorisation.authenticator
    }

    authenticate(authenticator)
  }
}


trait BaseResourceBuilder {

  def create(actorContext: ActorContext, auth: Authorization, eventBus: SchmetteringEventBus): BaseResource

  /**
   * override if resource need to be initialized. For example db table creation etc
   */
  def init()(implicit ec: ExecutionContext): Unit = {}
}

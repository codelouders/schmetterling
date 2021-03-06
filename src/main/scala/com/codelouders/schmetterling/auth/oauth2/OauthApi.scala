/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.auth.oauth2

import java.util.concurrent.TimeUnit

import akka.pattern.ask
import akka.actor.{ActorRef, ActorContext}
import akka.util.Timeout

import spray.http.{StatusCodes, FormData}
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing.AuthenticationFailedRejection
import spray.routing.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}

import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.auth.oauth2.session._
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.{BaseResourceBuilder, BaseResource}
import com.codelouders.schmetterling.rest.auth.{Authorization, RestApiUser}


import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Company API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
class OauthApi(val actorContext: ActorContext, sessionManager: ActorRef, authUserDao: OauthUserDao,
               override val authorization: Authorization, eventBus: SchmetteringEventBus) extends BaseResource with Logging {

  private implicit val timeout = Timeout(1, TimeUnit.SECONDS)

  override val logTag: String = getClass.getName

  override def authorizedResource = false

  override protected def getResourceName: String = {
    ResourceName
  }

  override def route(implicit restApiUser: RestApiUser) =

    pathEnd {
      post {
        entity(as[FormData]) {
          user =>
            ctx => {
              val localCtx = ctx
              val grantType = user.fields.find(item => item._1 == "grant_type")
              grantType match{ // if (grantType.equals()){
                case Some(grant) if grant._2.equals("client_credentials") =>
                  val username = user.fields.find(item => item._1 == "username")
                  val password = user.fields.find(item => item._1 == "password")

                  (username, password) match {
                    case (Some(login), Some(pass)) =>
                      val loginProccess = sessionManager ? Create(OauthUser(None, username = login._2, password = pass._2))
                      loginProccess.mapTo[Session].onComplete {
                        case Success(result) =>
                          localCtx.complete(TokenResponse(result.token.accessToken, SessionManager.LifeTimeInSec))
                        case Failure(e) =>
                          e match {
                            case IncorrectLogin => localCtx.reject (AuthenticationFailedRejection (CredentialsRejected, List ()))
                            case t: Exception => localCtx.complete(t)
                          }
                      }
                    case _: Any =>
                      localCtx.reject (AuthenticationFailedRejection (CredentialsMissing, List ()))
                  }


                case any: Any =>
                  localCtx.complete(StatusCodes.BadRequest, "grant_type is missing or unknown type")
              }
            }
        }
      }
    }
}

class OauthApiBuilder extends BaseResourceBuilder{

  val authUserDao = new OauthUserDao

  override def create(actorContext: ActorContext, authorization: Authorization, eventBus: SchmetteringEventBus): BaseResource = {
    new OauthApi(actorContext, SessionService.getSessionManager, authUserDao, authorization, eventBus)
  }

  override def init()(implicit ec:ExecutionContext) = {
    authUserDao.initTable()
  }
}

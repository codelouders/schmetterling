package com.codelouders.schmetterling.auth.oauth2

import scala.concurrent.{Future, ExecutionContext}
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout

import spray.http.HttpRequest
import spray.routing.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import spray.routing.{AuthenticationFailedRejection, RequestContext}
import spray.routing.authentication.Authentication

import com.codelouders.schmetterling.auth.oauth2.session.{GetSession, Session, SessionService}
import com.codelouders.schmetterling.logger.Logging
import com.codelouders.schmetterling.rest.BaseResourceBuilder
import com.codelouders.schmetterling.rest.auth.{RestApiUser, Authorization}


/**
 * Created by wiktort on 18/08/2015.
 *
 * Created on 18/08/2015
 */
class OauthAuthorization(config: OauthConfig) extends Authorization with Logging {

  override val logTag: String = getClass.getName

  override def getAuthApiBuilder: Option[BaseResourceBuilder] = Some(new OauthApiBuilder)

  override def init()(implicit actorSystem: ActorSystem) {
    SessionService.init(config.authorizationProvider)
  }

  override def authFunction(implicit executionContext: ExecutionContext): (RequestContext) => Future[Authentication[RestApiUser]] = {
    requestContext =>
      try {
        val token = OauthRequestParser.getToken(requestContext.request)
        implicit val timeout = Timeout(1, TimeUnit.SECONDS)
        (SessionService.getSessionManager ? new GetSession(token)).recover{case e: Exception => None}.mapTo[Option[Session]].map {
          case Some(res) => Right(res.user)
          case None => Left(AuthenticationFailedRejection(CredentialsRejected, List()))
        }
      } catch {
        case e: AuthHeaderIsMissingException =>
          L.debug(e.getMessage)
          Future.successful(Left(AuthenticationFailedRejection(CredentialsMissing, List())))

        case e: IncorrectAuthenticationHeaderException =>
          L.debug(e.getMessage)
          Future.successful(Left(AuthenticationFailedRejection(CredentialsMissing, List())))

        case e: UnknownTokenTypeException =>
          L.debug(e.getMessage)
          Future.successful(Left(AuthenticationFailedRejection(CredentialsRejected, List())))
      }
  }
}

object OauthRequestParser {

  val AuthorizationHeader = "Authorization"

  def tokenExists(request: HttpRequest) = request.headers.exists(_.name == AuthorizationHeader)

  def getToken(request: HttpRequest): String = {
    val tokenHeaderOption = request.headers.find(_.name == AuthorizationHeader)
    tokenHeaderOption match {
      case Some(tokenHeader) if tokenHeader.value.nonEmpty =>
        val authData = tokenHeader.value.split(" ")
        if (authData.size != 2) {
          throw new IncorrectAuthenticationHeaderException()
        }

        authData(0) match {
          case "Bearer" =>
            authData(1)

          case anyOther =>
            throw new UnknownTokenTypeException()

        }

      case Some(tokenHeader) =>
        throw new TokenIsMissingException()

      case None =>
        throw new AuthHeaderIsMissingException()
    }
  }
}


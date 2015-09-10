/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.schmetterling.integration

import akka.actor.ActorContext

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import org.scalatest.mock.MockitoSugar

import com.codelouders.schmetterling.events.SchmetteringEventBus
import com.codelouders.schmetterling.example.api.company.{Company, CompanyApi, CompanyApiBuilder, CompanyDao}
import com.codelouders.schmetterling.auth.oauth2.{OauthUser, OauthApiBuilder, OauthApi, OauthUserDao}
import com.codelouders.schmetterling.auth.oauth2.provider.MysqlAuthorizationProvider
import com.codelouders.schmetterling.auth.oauth2.session.SessionService
import com.codelouders.schmetterling.rest.auth.{NoAuthorisation, Authorization}


trait Mocking extends MockitoSugar {
  val companyDb = mock[CompanyDao]
  val companyApi = mock[CompanyApiBuilder]
  val oauthApi = mock[OauthApiBuilder]
  val oauthDao = mock[OauthUserDao]
  val oauthProvider = mock[MysqlAuthorizationProvider]
  val eventBus = mock[SchmetteringEventBus]


  when(companyApi.create(any(classOf[ActorContext]), any(classOf[Authorization]), any(classOf[SchmetteringEventBus]))).thenAnswer(new Answer[CompanyApi] {
    override def answer(invocationOnMock: InvocationOnMock): CompanyApi = {
      val args = invocationOnMock.getArguments
      new CompanyApi(args(0).asInstanceOf[ActorContext], companyDb, NoAuthorisation, eventBus)
    }
  })

  when(oauthApi.create(any(classOf[ActorContext]), any(classOf[Authorization]), any(classOf[SchmetteringEventBus]))).thenAnswer(new Answer[OauthApi] {
    override def answer(invocationOnMock: InvocationOnMock): OauthApi = {
      val args = invocationOnMock.getArguments
      new OauthApi(args(0).asInstanceOf[ActorContext], SessionService.getSessionManager, oauthDao, NoAuthorisation, eventBus)
    }
  })

  when(companyDb.create(any())).thenReturn(1)
  when(companyDb.getById(any())).thenAnswer(new Answer[Option[Company]] {
    override def answer(invocationOnMock: InvocationOnMock): Option[Company] = {
      val args = invocationOnMock.getArguments
      if (args(0).asInstanceOf[Int] == 1){
        Option(new Company(Some(1), "test name", "test address"))
      } else {
        None
      }
    }
  })

  when(companyDb.getAll).thenReturn(List(new Company(Some(1), "test name", "test address"),
    new Company(Some(2), "test name", "test address")))
  when(companyDb.deleteById(any())).thenAnswer(new Answer[Int] {
    override def answer(invocationOnMock: InvocationOnMock): Int = {
      val args = invocationOnMock.getArguments
      if (args(0).asInstanceOf[Int] == 1){
        1
      } else {
        0
      }
    }
  })

  when(companyDb.update(any())).thenAnswer(new Answer[Int] {
    override def answer(invocationOnMock: InvocationOnMock): Int = {
      val args = invocationOnMock.getArguments
      if (args(0).asInstanceOf[Company].getId == 1){
        1
      } else {
        0
      }
    }
  })


  when(companyDb.patch(any(), any())).thenAnswer(new Answer[List[Int]] {
    override def answer(invocationOnMock: InvocationOnMock): List[Int] = {
      val args = invocationOnMock.getArguments
      if (args(1).asInstanceOf[Int] == 1){
        List(1)
      } else {
        List(0)
      }
    }
  })

  when(oauthProvider.login(any())).thenReturn(Some(new OauthUser(Some(1), "me", "passhash")))
}

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.auth.oauth2

import scala.slick.driver.MySQLDriver.simple._

import com.codelouders.schmetterling.database.{BaseT, BaseDbEntity}


class OauthUserDao extends BaseDbEntity[OauthUser, OauthUserT](ResourceName, TableQuery[OauthUserT])


class OauthUserT(tag: Tag) extends BaseT[OauthUser](tag, ResourceName) {
  def username: Column[String] = column[String]("username")
  def password: Column[String] = column[String]("password")

  override def * = (id.?, username, password) <> (
    (OauthUser.apply _).tupled, OauthUser.unapply)
}

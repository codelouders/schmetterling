package com.codelouders.schmetterling.rest.auth


/**
 * Created by wiktort on 18/08/2015.
 *
 * Created on 18/08/2015
 */
trait RestApiUser{
  val id: Option[Int]
}

object NoAuthUser extends RestApiUser {
  override val id: Option[Int] = Some(1)
}

trait AuthenticatedUser extends RestApiUser {
  def username(): String
  def password(): String
}
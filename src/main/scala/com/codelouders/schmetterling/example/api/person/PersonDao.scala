/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.schmetterling.example.api.person

import scala.slick.driver.MySQLDriver.simple._

import com.codelouders.schmetterling.database.{BaseT, BaseDbEntity}


class PersonDao extends BaseDbEntity[Person, PersonT]("person", TableQuery[PersonT])


class PersonT(tag: Tag) extends BaseT[Person](tag, "person") {
  def name: Column[String] = column[String]("name")
  def lastname: Column[String] = column[String]("lastname")

  override def * = (id.?, name, lastname) <> (
    (Person.apply _).tupled, Person.unapply)
}

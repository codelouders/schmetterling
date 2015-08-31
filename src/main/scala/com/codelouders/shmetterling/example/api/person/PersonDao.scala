/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.codelouders.shmetterling.example.api.person

import com.codelouders.shmetterling.database.{BaseT, BaseDbEntity}
import scala.slick.driver.MySQLDriver.simple._

class PersonDao extends BaseDbEntity[Person, PersonT]("person", TableQuery[PersonT])

class PersonT(tag: Tag) extends BaseT[Person](tag, "person") {
  def name: Column[String] = column[String]("name")
  def lastname: Column[String] = column[String]("lastname")

  override def * = (id.?, name, lastname) <> (
    (Person.apply _).tupled, Person.unapply)
}

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api.company

import scala.slick.driver.MySQLDriver.simple._

import com.codelouders.schmetterling.database.{BaseT, BaseDbEntity}


class CompanyDao extends BaseDbEntity[Company, CompanyT](ResourceName, TableQuery[CompanyT])

class CompanyT(tag: Tag) extends BaseT[Company](tag, ResourceName) {
  def name: Column[String] = column[String]("name")
  def address: Column[String] = column[String]("address")

  override def * = (id.?, name, address) <> (
    (Company.apply _).tupled, Company.unapply)
}

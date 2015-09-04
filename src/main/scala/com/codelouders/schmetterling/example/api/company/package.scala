/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api

import spray.json.DefaultJsonProtocol._

import scala.slick.driver.MySQLDriver.simple._

import com.codelouders.schmetterling.entity.BaseEntity


package object company {

  val ResourceName = "company"

  case class Company(id: Option[Int] = None, name: String, address: String) extends BaseEntity

  implicit val CompanyFormat = jsonFormat3(Company)
}

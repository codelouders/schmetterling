/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.example.api

import spray.json.DefaultJsonProtocol._

import com.codelouders.schmetterling.entity.BaseEntity


package object person {

  val ResourceName = "person"

  case class Person(id: Option[Int] = None, name: String, lastname: String) extends BaseEntity

  implicit val PersonFormat = jsonFormat3(Person)

  implicit val DeleteFormat = jsonFormat1(DeleteResult)


}

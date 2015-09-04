/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.database

import scala.concurrent.ExecutionContext

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.meta.MTable

import com.codelouders.schmetterling.entity.{JsonNotation, BaseEntity}
import com.codelouders.schmetterling.rest.UpdateException
import com.codelouders.schmetterling.util.SqlUtil
import com.codelouders.schmetterling.entity.BaseEntity


class BaseDbEntity[T <: BaseEntity, R <: BaseT[T]](val tableName: String, val tableQuery: TableQuery[R]) extends DatabaseAccess {

  val createReturningId = tableQuery returning tableQuery.map{item => item.id}

  def create(entity: T): Int = {
    connectionPool withSession {
      implicit session =>
        val resId = createReturningId += entity
        resId
    }
  }

  def getAll = {
    connectionPool withSession {
      implicit session =>
        tableQuery.list
    }
  }

  def getById(id: Int) = {
    connectionPool withSession {
      implicit session =>
        tableQuery.filter(_.id === id).firstOption
    }
  }

  def update(entity: T): Int = {
    connectionPool withSession {
      implicit session =>
        tableQuery.filter(_.id === entity.id).update(entity)
    }
  }

  def patch(listOfPatches: List[JsonNotation], id: Int): List[Int] = {
    connectionPool withTransaction {
      implicit transaction =>
        listOfPatches.map {
          patch =>
            val updateStatement = s"${SqlUtil.patch2updateStatement(tableName, patch)} ${SqlUtil.whereById(id)}"
            try {
              Q.updateNA(updateStatement).first
            } catch {
              case e: MySQLIntegrityConstraintViolationException =>
                throw UpdateException(e.getMessage)
            }
        }
    }
  }

  def deleteById(id: Int):Int = {
    connectionPool withSession {
      implicit session =>
        tableQuery.filter(_.id === id).delete
    }
  }

  def runQuery(query: String): Int = {
    connectionPool withSession {
      implicit session =>
        Q.updateNA(query).first
    }
  }

  def initTable()(implicit ec: ExecutionContext): Unit = {
    connectionPool withSession {
      implicit session =>
        if (MTable.getTables(tableName).list.isEmpty) {
          tableQuery.ddl.create
        }
    }
  }

}

abstract class BaseT[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
}

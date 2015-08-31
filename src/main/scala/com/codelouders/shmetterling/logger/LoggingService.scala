/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.logger

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import com.codelouders.shmetterling.logger.LoggingService.LoggerSettings
import com.codelouders.shmetterling.util.ConfigUtil
import com.typesafe.config.Config

/**
 * Logging service 
 * 
 * @param loggers - list of loggers
 */
class LoggingService(loggers: List[Logger], loggerSettings: LoggerSettings) extends Actor {

  var packagesLoggingLevelCache = Map.empty[String, LogLevel]

  override def receive: Receive = {
    case Debug(msg, className, tag) =>
      if (getLogLevel(tag) == DebugLevel){
        loggers.foreach{ _.debug(msg, tag) }
      }

    case Info(msg, className, tag) =>
      if (getLogLevel(tag) == InfoLevel){
        loggers.foreach{ _.info(msg, tag)}
      }

    case Warning(msg, className, tag) =>
      if (getLogLevel(tag) == WarningLevel){
        loggers.foreach{ _.warn(msg, tag)}
      }

    case Error(msg, className, tag, cause, stack) =>
      if (getLogLevel(tag) == ErrorLevel){
        loggers.foreach{ _.error(msg, tag, cause, stack)}
      }

    case any: Any =>
      throw new Exception("Log type message unknown")


  }

  private def getLogLevel(className: String):LogLevel = {

    packagesLoggingLevelCache.get(className) match {
      case Some(logLevel) =>
        logLevel
      case None =>
        val logLevel = checkoutLogLevel(className)
        packagesLoggingLevelCache += (className -> logLevel)
        logLevel
    }
  }

  private def checkoutLogLevel(className: String) = {
    loggerSettings.packageLevels match {
      case Some(packageLevels) =>
        ConfigUtil.getString(packageLevels, className.substring(0, className.lastIndexOf("."))) match {
          case Some(lvl) =>
            LogLevel.fromString(lvl)
          case None =>
            loggerSettings.systemLevelLogging

        }
      case None =>
        loggerSettings.systemLevelLogging
    }
  }
}

object LoggingService {

  type PackageName = String

  case class LoggerSettings(systemLevelLogging: LogLevel, packageLevels: Option[Config])


  private var loggingService: ActorRef = null
  val LoggerActorName = "Logger-Actor"

  private def props(handlers: List[Logger], loggerSettings: LoggerSettings) = {
    Props(classOf[LoggingService], handlers, loggerSettings)
  }

  def getLoggingService: ActorRef = {
    loggingService
  }

  def init(loggers: List[Logger], loggerSettings: Option[Config] = None)(implicit actorSystem: ActorSystem): Unit = {
    val settings = loggerSettings match {
      case Some(logSettings) =>
        val packageLevels = logSettings.hasPath("package-level") match {
          case true =>
            Some(logSettings.getConfig("package-level"))
          case false =>
            None
        }
        LoggerSettings(LogLevel.fromString(logSettings.getString("level")), packageLevels)
      case None =>
        LoggerSettings(DebugLevel, None)
    }

    loggingService = actorSystem.actorOf(LoggingService.props(loggers, settings), LoggingService.LoggerActorName)
  }

}

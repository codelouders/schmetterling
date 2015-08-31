/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.logger

/**
 * Logging levels
 */
private[logger] sealed trait LogLevel
case object DebugLevel extends LogLevel
case object InfoLevel extends LogLevel
case object WarningLevel extends LogLevel
case object ErrorLevel extends LogLevel

object LogLevel {
  def fromString(levelString: String): LogLevel = {
    levelString match {
      case "DEBUG" => DebugLevel
      case "INFO" => InfoLevel
      case "WARNING" => WarningLevel
      case "ERROR" => ErrorLevel
      case any: Any =>
        throw new RuntimeException(levelString + " is unknown log level type!!!")
    }
  }
}
